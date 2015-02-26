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
import java.util.*;

/**
 * The <code>ICA_I</code> class provides a Java interface to the M-functions
 * from the files:
 * <pre>
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\FastICA.m
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\ICA_I.m
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\prewhitening_1.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a <code>ICA_I</code> instance 
 * when it is no longer needed to ensure that native resources allocated by this class 
 * are properly freed.
 * @version 0.0
 */
public class ICA_I extends MWComponentInstance<ICA_I>
{
    /**
     * Tracks all instances of this class to ensure their dispose method is
     * called on shutdown.
     */
    private static final Set<Disposable> sInstances = new HashSet<Disposable>();

    /**
     * Maintains information used in calling the <code>FastICA</code> M-function.
     */
    private static final MWFunctionSignature sFastICASignature =
        new MWFunctionSignature(/* max outputs = */ 1,
                                /* has varargout = */ false,
                                /* function name = */ "FastICA",
                                /* max inputs = */ 4,
                                /* has varargin = */ false);
    /**
     * Maintains information used in calling the <code>ICA_I</code> M-function.
     */
    private static final MWFunctionSignature sICA_ISignature =
        new MWFunctionSignature(/* max outputs = */ 1,
                                /* has varargout = */ false,
                                /* function name = */ "ICA_I",
                                /* max inputs = */ 4,
                                /* has varargin = */ false);
    /**
     * Maintains information used in calling the <code>prewhitening_1</code> M-function.
     */
    private static final MWFunctionSignature sPrewhitening_1Signature =
        new MWFunctionSignature(/* max outputs = */ 2,
                                /* has varargout = */ false,
                                /* function name = */ "prewhitening_1",
                                /* max inputs = */ 2,
                                /* has varargin = */ false);

    /**
     * Shared initialization implementation - private
     */
    private ICA_I (final MWMCR mcr) throws MWException
    {
        super(mcr);
        // add this to sInstances
        synchronized(ICA_I.class) {
            sInstances.add(this);
        }
    }

    /**
     * Constructs a new instance of the <code>ICA_I</code> class.
     */
    public ICA_I() throws MWException
    {
        this(ICA_IMCRFactory.newInstance());
    }
    
    private static MWComponentOptions getPathToComponentOptions(String path)
    {
        MWComponentOptions options = new MWComponentOptions(new MWCtfExtractLocation(path),
                                                            new MWCtfDirectorySource(path));
        return options;
    }
    
    /**
     * @deprecated Please use the constructor {@link #ICA_I(MWComponentOptions componentOptions)}.
     * The <code>com.mathworks.toolbox.javabuilder.MWComponentOptions</code> class provides API to set the
     * path to the component.
     * @param pathToComponent Path to component directory.
     */
    public ICA_I(String pathToComponent) throws MWException
    {
        this(ICA_IMCRFactory.newInstance(getPathToComponentOptions(pathToComponent)));
    }
    
    /**
     * Constructs a new instance of the <code>ICA_I</code> class. Use this constructor to 
     * specify the options required to instantiate this component.  The options will be 
     * specific to the instance of this component being created.
     * @param componentOptions Options specific to the component.
     */
    public ICA_I(MWComponentOptions componentOptions) throws MWException
    {
        this(ICA_IMCRFactory.newInstance(componentOptions));
    }
    
    /** Frees native resources associated with this object */
    public void dispose()
    {
        try {
            super.dispose();
        } finally {
            synchronized(ICA_I.class) {
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
            MWMCR mcr = ICA_IMCRFactory.newInstance();
            mcr.runMain( sFastICASignature, args);
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
        synchronized(ICA_I.class) {
            for (Disposable i : sInstances) i.dispose();
            sInstances.clear();
        }
    }

    /**
     * Provides the interface for calling the <code>FastICA</code> M-function 
     * where the first input, an instance of List, receives the output of the M-function and
     * the second input, also an instance of List, provides the input to the M-function.
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
    public void FastICA(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sFastICASignature);
    }

    /**
     * Provides the interface for calling the <code>FastICA</code> M-function 
     * where the first input, an Object array, receives the output of the M-function and
     * the second input, also an Object array, provides the input to the M-function.
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
    public void FastICA(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sFastICASignature);
    }

    /**
     * Provides the standard interface for calling the <code>FastICA</code>
     * M-function with 4 input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     * @return Array of length nargout containing the function outputs. Outputs
     * are returned as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>. Each output array
     * should be freed by calling its <code>dispose()</code> method.
     * @throws MWException An error has occurred during the function call.
     */
    public Object[] FastICA(int nargout, Object... rhs) throws MWException
    {
        Object[] lhs = new Object[nargout];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sFastICASignature), 
                    sFastICASignature);
        return lhs;
    }
    /**
     * Provides the interface for calling the <code>ICA_I</code> M-function 
     * where the first input, an instance of List, receives the output of the M-function and
     * the second input, also an instance of List, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %ICA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\',37)
     * %par_name='yale';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\';
     * %par_K=37;%选择投影轴数
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
    public void ICA_I(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sICA_ISignature);
    }

    /**
     * Provides the interface for calling the <code>ICA_I</code> M-function 
     * where the first input, an Object array, receives the output of the M-function and
     * the second input, also an Object array, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %ICA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\',37)
     * %par_name='yale';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\';
     * %par_K=37;%选择投影轴数
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
    public void ICA_I(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sICA_ISignature);
    }

    /**
     * Provides the standard interface for calling the <code>ICA_I</code>
     * M-function with 4 input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %ICA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\',37)
     * %par_name='yale';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\';
     * %par_K=37;%选择投影轴数
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
    public Object[] ICA_I(int nargout, Object... rhs) throws MWException
    {
        Object[] lhs = new Object[nargout];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sICA_ISignature), 
                    sICA_ISignature);
        return lhs;
    }
    /**
     * Provides the interface for calling the <code>prewhitening_1</code> M-function 
     * where the first input, an instance of List, receives the output of the M-function and
     * the second input, also an instance of List, provides the input to the M-function.
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
    public void prewhitening_1(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sPrewhitening_1Signature);
    }

    /**
     * Provides the interface for calling the <code>prewhitening_1</code> M-function 
     * where the first input, an Object array, receives the output of the M-function and
     * the second input, also an Object array, provides the input to the M-function.
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
    public void prewhitening_1(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sPrewhitening_1Signature);
    }

    /**
     * Provides the standard interface for calling the <code>prewhitening_1</code>
     * M-function with 2 input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     * @return Array of length nargout containing the function outputs. Outputs
     * are returned as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>. Each output array
     * should be freed by calling its <code>dispose()</code> method.
     * @throws MWException An error has occurred during the function call.
     */
    public Object[] prewhitening_1(int nargout, Object... rhs) throws MWException
    {
        Object[] lhs = new Object[nargout];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sPrewhitening_1Signature), 
                    sPrewhitening_1Signature);
        return lhs;
    }
}
