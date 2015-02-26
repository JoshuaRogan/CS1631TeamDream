/*
 * MATLAB Compiler: 4.14 (R2010b)
 * Date: Wed Mar 07 15:18:06 2012
 * Arguments: "-B" "macro_default" "-W" "java:ICA_I,ICA_I" "-T" "link:lib" "-d" 
 * "C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\ICA_I\\src" 
 * "-w" "enable:specified_file_mismatch" "-w" "enable:repeated_file" "-w" 
 * "enable:switch_ignored" "-w" "enable:missing_lib_sentinel" "-w" "enable:demo_license" 
 * "-v" 
 * "class{ICA_I:C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\FastICA.m,C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\ICA_I.m,C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\prewhitening_1.m}" 
 */

package ICA_I;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class ICA_IMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "ICA_I_682407F8385A9E83D84A0DD0103C4AFA";
    
    /** Component name */
    private static final String sComponentName = "ICA_I";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(ICA_IMCRFactory.class)
        );
    
    
    private ICA_IMCRFactory()
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
            ICA_IMCRFactory.class, 
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
