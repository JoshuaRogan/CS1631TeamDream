

/**
 * Vital data (Waveform data)
 * 
 * @author Yinglin Sun
 *
 */
public class VitalData {
	private int systolic;
	private int diastolic;
	private int mean_pressure;
	private int pulse;
	private int spo2;
	
	public VitalData(String ptStr) {
		// parse pt string, "PT****"
		String syst = ptStr.substring(2, 5).trim();
		if (syst.length() == 0)
			this.systolic = 0;
		else
			this.systolic = Integer.valueOf(syst);
		
		String dias = ptStr.substring(6,9).trim();
		if (dias.length() == 0)
			this.diastolic = 0;
		else
			this.diastolic = Integer.valueOf(dias);
		
		String mean = ptStr.substring(10, 13).trim();
		if (mean.length() == 0)
			this.mean_pressure = 0;
		else
			this.mean_pressure = Integer.valueOf(mean);
		
		String pulse = ptStr.substring(14, 17).trim();
		if (pulse.length() ==0)
			this.pulse = 0;
		else
			this.pulse = Integer.valueOf(pulse);
		
		String spo2 = ptStr.substring(18, 21).trim();
		if (spo2.length() ==0)
			this.spo2 = 0;
		else
			this.spo2 = Integer.valueOf(spo2);
	}

	public int getSystolic() {
		return systolic;
	}

	public int getDiastolic() {
		return diastolic;
	}

	public int getMean_pressure() {
		return mean_pressure;
	}

	public int getPulse() {
		return pulse;
	}

	public int getSpo2() {
		return spo2;
	}
	
	public boolean isValidBpress() {
		return this.getSystolic() > 0 && this.getDiastolic() > 0;
	}
	
	public boolean equalBpress(VitalData vd) {
		return vd!=null && this.getSystolic()==vd.getSystolic() 
				&& this.getDiastolic()==vd.getDiastolic();
	}
	
	public boolean equalSpo2(VitalData vd) {
		return vd !=null && this.getSpo2()==vd.getSpo2();
	}

	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Systolic: ").append(this.systolic).append("; ");
		sbuf.append("Diastolic: ").append(this.diastolic).append("; ");
		sbuf.append("Mean: ").append(this.mean_pressure).append("; ");
		sbuf.append("Pulse: ").append(this.pulse).append("; ");
		sbuf.append("SpO2: ").append(this.spo2);
		
		return sbuf.toString();
	}

	public boolean isValidSPO2() {
		return this.getSpo2() > 0;
	}

}
