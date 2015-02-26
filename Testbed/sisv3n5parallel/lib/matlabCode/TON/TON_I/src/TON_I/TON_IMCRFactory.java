/*
 * MATLAB Compiler: 4.14 (R2010b)
 * Date: Wed Mar 07 15:22:17 2012
 * Arguments: "-B" "macro_default" "-W" "java:TON_I,TON_I" "-T" "link:lib" "-d" 
 * "C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\TON\\TON_I\\src" 
 * "-w" "enable:specified_file_mismatch" "-w" "enable:repeated_file" "-w" 
 * "enable:switch_ignored" "-w" "enable:missing_lib_sentinel" "-w" "enable:demo_license" 
 * "-v" 
 * "class{TON_I:C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\TON\\TON_I.m}" 
 */

package TON_I;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class TON_IMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "TON_I_67103FB0E1396063C114CF7D7FACEF45";
    
    /** Component name */
    private static final String sComponentName = "TON_I";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(TON_IMCRFactory.class)
        );
    
    
    private TON_IMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            TON_IMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{7,14,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
