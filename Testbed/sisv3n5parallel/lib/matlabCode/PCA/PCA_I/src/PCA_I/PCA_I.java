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
import java.util.*;

/**
 * The <code>PCA_I</code> class provides a Java interface to the M-functions
 * from the files:
 * <pre>
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\PCA\\PCA_I.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a <code>PCA_I</code> instance 
 * when it is no longer needed to ensure that native resources allocated by this class 
 * are properly freed.
 * @version 0.0
 */
public class PCA_I extends MWComponentInstance<PCA_I>
{
    /**
     * Tracks all instances of this class to ensure their dispose method is
     * called on shutdown.
     */
    private static final Set<Disposable> sInstances = new HashSet<Disposable>();

    /**
     * Maintains information used in calling the <code>PCA_I</code> M-function.
     */
    private static final MWFunctionSignature sPCA_ISignature =
        new MWFunctionSignature(/* max outputs = */ 1,
                                /* has varargout = */ false,
                                /* function name = */ "PCA_I",
                                /* max inputs = */ 4,
                                /* has varargin = */ false);

    /**
     * Shared initialization implementation - private
     */
    private PCA_I (final MWMCR mcr) throws MWException
    {
        super(mcr);
        // add this to sInstances
        synchronized(PCA_I.class) {
            sInstances.add(this);
        }
    }

    /**
     * Constructs a new instance of the <code>PCA_I</code> class.
     */
    public PCA_I() throws MWException
    {
        this(PCA_IMCRFactory.newInstance());
    }
    
    private static MWComponentOptions getPathToComponentOptions(String path)
    {
        MWComponentOptions options = new MWComponentOptions(new MWCtfExtractLocation(path),
                                                            new MWCtfDirectorySource(path));
        return options;
    }
    
    /**
     * @deprecated Please use the constructor {@link #PCA_I(MWComponentOptions componentOptions)}.
     * The <code>com.mathworks.toolbox.javabuilder.MWComponentOptions</code> class provides API to set the
     * path to the component.
     * @param pathToComponent Path to component directory.
     */
    public PCA_I(String pathToComponent) throws MWException
    {
        this(PCA_IMCRFactory.newInstance(getPathToComponentOptions(pathToComponent)));
    }
    
    /**
     * Constructs a new instance of the <code>PCA_I</code> class. Use this constructor to 
     * specify the options required to instantiate this component.  The options will be 
     * specific to the instance of this component being created.
     * @param componentOptions Options specific to the component.
     */
    public PCA_I(MWComponentOptions componentOptions) throws MWException
    {
        this(PCA_IMCRFactory.newInstance(componentOptions));
    }
    
    /** Frees native resources associated with this object */
    public void dispose()
    {
        try {
            super.dispose();
        } finally {
            synchronized(PCA_I.class) {
                sInstances.remove(this);
            }
        }
    }
  
    /**
     * Invokes the first m-function specified by MCC, with any arguments given on
     * the command line, and prints the result.
     */
    public static void main (String[] args)
    {
        try {
            MWMCR mcr = PCA_IMCRFactory.newInstance();
            mcr.runMain( sPCA_ISignature, args);
            mcr.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * Calls dispose method for each outstanding instance of this class.
     */
    public static void disposeAllInstances()
    {
        synchronized(PCA_I.class) {
            for (Disposable i : sInstances) i.dispose();
            sInstances.clear();
        }
    }

    /**
     * Provides the interface for calling the <code>PCA_I</code> M-function 
     * where the first input, an instance of List, receives the output of the M-function and
     * the second input, also an instance of List, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %PCA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\',0.2)
     * %par_name='orl';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\yalefaces\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\';
     * %par_P=0.2;特征向量占图像尺寸的比率
     * </pre>
     * </p>
     * @param lhs List in which to return outputs. Number of outputs (nargout) is
     * determined by allocated size of this List. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs List containing inputs. Number of inputs (nargin) is determined
     * by the allocated size of this List. Input arguments may be passed as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or
     * as arrays of any supported Java type. Arguments passed as Java types are
     * converted to MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void PCA_I(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sPCA_ISignature);
    }

    /**
     * Provides the interface for calling the <code>PCA_I</code> M-function 
     * where the first input, an Object array, receives the output of the M-function and
     * the second input, also an Object array, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %PCA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\',0.2)
     * %par_name='orl';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\yalefaces\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\';
     * %par_P=0.2;特征向量占图像尺寸的比率
     * </pre>
     * </p>
     * @param lhs array in which to return outputs. Number of outputs (nargout)
     * is determined by allocated size of this array. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs array containing inputs. Number of inputs (nargin) is
     * determined by the allocated size of this array. Input arguments may be
     * passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void PCA_I(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sPCA_ISignature);
    }

    /**
     * Provides the standard interface for calling the <code>PCA_I</code>
     * M-function with 4 input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %PCA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\',0.2)
     * %par_name='orl';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\yalefaces\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\PCA\\';
     * %par_P=0.2;特征向量占图像尺寸的比率
     * </pre>
     * </p>
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     * @return Array of length nargout containing the function outputs. Outputs
     * are returned as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>. Each output array
     * should be freed by calling its <code>dispose()</code> method.
     * @throws MWException An error has occurred during the function call.
     */
    public Object[] PCA_I(int nargout, Object... rhs) throws MWException
    {
        Object[] lhs = new Object[nargout];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sPCA_ISignature), 
                    sPCA_ISignature);
        return lhs;
    }
}
