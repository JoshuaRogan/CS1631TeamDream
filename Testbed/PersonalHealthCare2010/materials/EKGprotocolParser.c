// ksi_protocol_parser.c

/*
Parse KSI protocol test file coming from stdin.

There is no warranty for this software.
It is provided for protocol reception testing convenience only.
This software has not been documented, reviewed, validated, or verified.
Use this software at your own risk.

CUSTOM PROTOCOL FOR KSI
=======================

SERIAL DATA: 9600 BAUD, 8 DATA BITS, NO PARITY, 1 STOP BIT

THERE ARE TWO MESSAGE TYPES.

THE 8 BYTE WAVEFORM MESSAGE BEGINS WITH A BYTE OF 0x80.
THE VITAL SIGN MESSAGE BEGINS WITH "PT".
THE "PT" MESSAGE NEVER CONTAINS 0x80.
THE "PT" MESSAGE CONTAINS ONLY "P", "T", " ", and "0" through "9".

=======================
VITAL SIGN DATA MESSAGE
=======================

EMITTED AT 10 SECOND INTERVALS

PARAMETER = BLANKS IF NOT VALID
PARAMETER = NUMERIC IF VALID

ALARM = 0 IF NO ALARM
ALARM = 1 IF ALARM

UNIT = 0 IF CELSIUS
UNIT = 1 IF FAHRENHEIT

ITEM		START	LEN	ALARM	UNIT
====		=====	===	=====	====
PT FLAG		0	2	--	--
SYSTOLIC 	2	4	3	--
DIASTOLIC 	6	4	3	--
MEAN 		10	4	3	--
PULSE		14	4	3	--
SPO2 		18	4	3	--
INCO2 		22	4	3	--
INAGT 		26	4	3	--
FIO2 		30	4	3	--
ETCO2 		34	4	3	--
ETAGT 		38	4	3	--
N2O 		42	4	3	--
T1 		46	6	4	5
T2 		52	6	4	5
TD 		58	6	4	5
IP1MAX 		64	4	3	--
IP1MIN 		68	4	3	--
IP1MEAN 	72	4	3	--
IP2MAX 		76	4	3	--
IP2MIN 		80	4	3	--
IP2MEAN 	84	4	3	--
RESP 		88	4	3	--

================
WAVEFORM MESSAGE
================

LENGTH 8 BYTES
EMITTED AT AN AVERAGE INTERVAL OF 10 MILLISECONDS

STATUS BIT DEFINITIONS
======================
BIT 0 MESSAGE SEQUENCE LOW BIT
BIT 1 MESSAGE SEQUENCE HIGH BIT
BIT 2 LEAD(S) OFF == 1, LEADS ON == 0
BIT 3 5-LEAD == 1, 3-LEAD == 0
BIT 4 - BIT 7 UNUSED, MUST BE ZERO

MESSAGE SEQUENCE COUNTS MODULO 4: 0, 1, 2, 3, 0, 1, 2, 3, ...

LEAD DATA RANGE IS 0x00 TO 0xFF

BYTE	VALUE	ITEM
====	=====	====
0	0x80	FLAG/TYPE
1	0xNN	STATUS
2	0xNN	LEAD I HIGH BYTE
3	0xNN	LEAD I LOW BYTE
4	0xNN	LEAD II HIGH BYTE
5	0xNN	LEAD II LOW BYTE
6	0xNN	LEAD V HIGH BYTE
7	0xNN	LEAD V LOW BYTE

*/

///////////////////////////////////////////////////////////
// READ.ME
///////////////////////////////////////////////////////////

// Build command line for Linux (works on FC5):
// -Wall means full warnings
// -g3 means full debug info in image
// gcc -Wall -g3 ksi_protocol_parser.c -o kppt

// If "1", provide data input capture to stdout as binary.
// ./kppt > data.bin
#define SAVE_BINARY_INPUT 0

// If "1", read binary data from stdin
// ./kppt < data.bin
#define READ_STDIN 1

// If "1", format protocol data for spreadsheet input or printout.
// ./kppt > data.txt
#define PRINT_PARSED_DATA 1

///////////////////////////////////////////////////////////

#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <termios.h>
#include <fcntl.h>

#define FALSE 0
#define TRUE 1

typedef enum tagSTATE
{
	ST_IDLE = 0,
	ST_ECG_STATUS = 1,
	ST_ECG_DATA = 2,
	ST_PT_FLAG = 3,
	ST_PT_DATA = 4
} STATE;

#define SEQ_MASK 0x03
#define LEAD_MASK 0x04
#define LEAD_SHIFT 2
#define LEADNUM_MASK 0x08
#define LEADNUM_SHIFT 3
#define MUST_BE_ZERO_MASK 0xF0

#define BUFSIZE 256
#define ECG_BUFSIZE 8
#define PT_BUFSIZE 92

int done;
int n;
int i;
int state;
unsigned char buf[BUFSIZE];
unsigned char ecg_buf[ECG_BUFSIZE];
unsigned char pt_buf[PT_BUFSIZE];
int ecg_index;
int pt_index;
int seq;
int synchronized;
int new_ecg_message;
int new_pt_message;
int leadoff;
int leadnum;
int ecgleadi;
int ecgleadii;
int ecgleadv;
int fd;
char *PortName;
speed_t baud;
struct termios* this_termios;
struct termios t;

int main(void)
{
	state = ST_IDLE;
	synchronized = FALSE;
	done = FALSE;
	new_ecg_message = FALSE;
	new_pt_message = FALSE;
	ecg_index = 0;
	pt_index = 0;

#if !READ_STDIN
	PortName = "/dev/ttyS0";
	baud = B9600;
	fd = open(PortName, O_RDWR);
	if (fd == -1)
	{
		printf("Can not open %s, error =%d\n", PortName, errno);
		return -1; // Fail.
	}
	this_termios = &t;
	(void) tcgetattr(fd, this_termios);

	cfmakeraw(this_termios);
	
	this_termios->c_iflag &= ~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON|IXOFF|IXANY);
	this_termios->c_oflag &= ~(OPOST);
	this_termios->c_lflag &= ~(ECHO|ECHONL|ICANON|ISIG|IEXTEN);
	this_termios->c_oflag &= 0;
	this_termios->c_lflag &= 0;

	this_termios->c_cflag |= ( CS8 | CREAD | CLOCAL);
	
	cfsetospeed(this_termios, baud);
	cfsetispeed(this_termios, baud);

	(void) tcsetattr(fd, TCSANOW, this_termios);
#else // #if !READ_STDIN
	fd = STDIN_FILENO;
#endif // #if !READ_STDIN
	while (!done)
	{
		// Get input stream:
		n = read(fd, &buf[0], BUFSIZE);
		if ((n < 0) && (errno == EINTR))
			n = read(fd, &buf[0], BUFSIZE);
		done = (n <= 0);
		if (done)
			break;
#if SAVE_BINARY_INPUT
		write(STDOUT_FILENO, &buf[0], n);
#endif // #if SAVE_BINARY_INPUT
		// Synchronize with message stream and extract messages:
		for (i = 0; i < n; i++)
		{
			switch (state)
			{
			case ST_IDLE:
				switch (buf[i])
				{
				case 0x80:
					ecg_index = 0;
					ecg_buf[ecg_index] = buf[i];
					ecg_index += 1;
					state = ST_ECG_STATUS;
					break;
				case 'P':
					pt_index = 0;
					pt_buf[pt_index] = buf[i];
					pt_index += 1;
					state = ST_PT_FLAG;
					break;
				case 'T':
					synchronized = FALSE;
					pt_buf[pt_index] = buf[i];
					pt_index += 1;
					state = ST_PT_DATA;
					break;
				default:
					synchronized = FALSE;
					break;
				}
				break;
			case ST_ECG_STATUS:
				if (buf[i] & MUST_BE_ZERO_MASK)
				{
					state = ST_IDLE;
				}
				if (synchronized)
				{
					if ((buf[i] & SEQ_MASK) != seq)
					{
						synchronized = FALSE;
						state = ST_IDLE;
						break;
					}
				}
				else
				{
					if ((buf[i] & SEQ_MASK) == seq)
					{
						synchronized = TRUE;
					}
				}
				seq = (buf[i] & SEQ_MASK);
				seq += 1;
				seq &= SEQ_MASK;
				ecg_buf[ecg_index] = buf[i];
				ecg_index += 1;
				state = ST_ECG_DATA;
				break;
			case ST_ECG_DATA:
				ecg_buf[ecg_index] = buf[i];
				ecg_index += 1;
				if (ecg_index >= ECG_BUFSIZE)
				{
					if (synchronized)
					{
						new_ecg_message = TRUE;
					}
					state = ST_IDLE;
				}
				break;
			case ST_PT_FLAG:
				if (buf[i] == 'T')
				{
					pt_buf[pt_index] = buf[i];
					pt_index += 1;
					state = ST_PT_DATA;
				}
				else
				{
					synchronized = FALSE;
					if (buf[i] == 0x80)
					{
						state = ST_ECG_STATUS;
						break;
					}
					state = ST_IDLE;
					break;
				}
				break;
			case ST_PT_DATA:
				if (buf[i] == 0x80)
				{
					synchronized = FALSE;
					state = ST_ECG_STATUS;
					break;
				}
				if (!((buf[i] == 0x20) ||
				    ((buf[i] >= 0x30) &&
				    (buf[i] <= 0x39))))
				{
					synchronized = FALSE;
					state = ST_IDLE;
					break;
				}
				pt_buf[pt_index] = buf[i];
				pt_index += 1;
				if (pt_index >= PT_BUFSIZE)
				{
					synchronized = TRUE;
					new_pt_message = TRUE;
					state = ST_IDLE;
				}
				break;
			default:
				synchronized = FALSE;
				state = ST_IDLE;
				break;
			}
			// Interpret messages:
			if (new_ecg_message)
			{
				new_ecg_message = FALSE;
				leadoff   = ((ecg_buf[1] & LEAD_MASK) >> LEAD_SHIFT);
				leadnum   = ((ecg_buf[1] & LEADNUM_MASK) >> LEADNUM_SHIFT);
				// Leads III, aVF, aVR, and aVL may be derived from I and II.
				ecgleadi  = ((ecg_buf[2] << 8) | ecg_buf[3]) - 32768;
				ecgleadii = ((ecg_buf[4] << 8) | ecg_buf[5]) - 32768;
				ecgleadv  = ((ecg_buf[6] << 8) | ecg_buf[7]) - 32768;
#if PRINT_PARSED_DATA
				printf("%d\t%d\t%d\t%d\t%d\n", leadoff, leadnum, ecgleadi, ecgleadii, ecgleadv);
#endif // #if PRINT_PARSED_DATA
			}
			if (new_pt_message)
			{
				new_pt_message = FALSE;
#if PRINT_PARSED_DATA
				printf("%*s\n", PT_BUFSIZE, &pt_buf[0]);
#endif // #if PRINT_PARSED_DATA
			}
		} // for (i = 0; i < n; i++)
	} // while (!done)
	return 0; // Success.
}
