/*
 * MATLAB Compiler: 4.14 (R2010b)
 * Date: Wed Mar 07 15:20:14 2012
 * Arguments: "-B" "macro_default" "-W" "java:PCA_I,PCA_I" "-T" "link:lib" "-d" 
 * "C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\PCA\\PCA_I\\src" 
 * "-w" "enable:specified_file_mismatch" "-w" "enable:repeated_file" "-w" 
 * "enable:switch_ignored" "-w" "enable:missing_lib_sentinel" "-w" "enable:demo_license" 
 * "-v" 
 * "class{PCA_I:C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\PCA\\PCA_I.m}" 
 */

package PCA_I;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class PCA_IMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "PCA_I_C5D61265344C66A8AF47F800377D10B3";
    
    /** Component name */
    private static final String sComponentName = "PCA_I";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(PCA_IMCRFactory.class)
        );
    
    
    private PCA_IMCRFactory()
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
            PCA_IMCRFactory.class, 
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
