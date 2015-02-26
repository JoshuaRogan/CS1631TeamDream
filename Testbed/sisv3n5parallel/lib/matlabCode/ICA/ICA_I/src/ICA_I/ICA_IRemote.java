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

import com.mathworks.toolbox.javabuilder.pooling.Poolable;
import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The <code>ICA_IRemote</code> class provides a Java RMI-compliant interface to the 
 * M-functions from the files:
 * <pre>
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\FastICA.m
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\ICA_I.m
 *  C:\\Users\\admin.sis\\Desktop\\SISv3\\Testbed\\sisv3n5parallel\\lib\\matlabCode\\ICA\\prewhitening_1.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a <code>ICA_IRemote</code> 
 * instance when it is no longer needed to ensure that native resources allocated by this 
 * class are properly freed, and the server-side proxy is unexported.  (Failure to call 
 * dispose may result in server-side threads not being properly shut down, which often 
 * appears as a hang.)  
 *
 * This interface is designed to be used together with 
 * <code>com.mathworks.toolbox.javabuilder.remoting.RemoteProxy</code> to automatically 
 * generate RMI server proxy objects for instances of ICA_I.ICA_I.
 */
public interface ICA_IRemote extends Poolable
{
    /**
     * Provides the standard interface for calling the <code>FastICA</code> M-function 
     * with 4 input arguments.  
     *
     * Input arguments to standard interface methods may be passed as sub-classes of 
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of any 
     * supported Java type (i.e. scalars and multidimensional arrays of any numeric, 
     * boolean, or character type, or String). Arguments passed as Java types are 
     * converted to MATLAB arrays according to default conversion rules.
     *
     * All inputs to this method must implement either Serializable (pass-by-value) or 
     * Remote (pass-by-reference) as per the RMI specification.
     *
     * No usage documentation is available for this function.  (To fix this, the function 
     * author should insert a help comment at the beginning of their M code.  See the 
     * MATLAB documentation for more details.)
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     *
     * @return Array of length nargout containing the function outputs. Outputs are 
     * returned as sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>. 
     * Each output array should be freed by calling its <code>dispose()</code> method.
     *
     * @throws java.jmi.RemoteException An error has occurred during the function call or 
     * in communication with the server.
     */
    public Object[] FastICA(int nargout, Object... rhs) throws RemoteException;
    /**
     * Provides the standard interface for calling the <code>ICA_I</code> M-function with 
     * 4 input arguments.  
     *
     * Input arguments to standard interface methods may be passed as sub-classes of 
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of any 
     * supported Java type (i.e. scalars and multidimensional arrays of any numeric, 
     * boolean, or character type, or String). Arguments passed as Java types are 
     * converted to MATLAB arrays according to default conversion rules.
     *
     * All inputs to this method must implement either Serializable (pass-by-value) or 
     * Remote (pass-by-reference) as per the RMI specification.
     *
     * M-documentation as provided by the author of the M function:
     * <pre>
     * 
     * %ICA_I('yale','E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\','E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\',37)
     * %par_name='yale';
     * %par_imgPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Face\\yale\\';
     * %par_rstPath='E:\\SIS\\workspace\\FaceRecognition\\Data\\Result\\ICA\\';
     * %par_K=37;%选择投影轴数
     * </pre>
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     *
     * @return Array of length nargout containing the function outputs. Outputs are 
     * returned as sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>. 
     * Each output array should be freed by calling its <code>dispose()</code> method.
     *
     * @throws java.jmi.RemoteException An error has occurred during the function call or 
     * in communication with the server.
     */
    public Object[] ICA_I(int nargout, Object... rhs) throws RemoteException;
    /**
     * Provides the standard interface for calling the <code>prewhitening_1</code> 
     * M-function with 2 input arguments.  
     *
     * Input arguments to standard interface methods may be passed as sub-classes of 
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of any 
     * supported Java type (i.e. scalars and multidimensional arrays of any numeric, 
     * boolean, or character type, or String). Arguments passed as Java types are 
     * converted to MATLAB arrays according to default conversion rules.
     *
     * All inputs to this method must implement either Serializable (pass-by-value) or 
     * Remote (pass-by-reference) as per the RMI specification.
     *
     * No usage documentation is available for this function.  (To fix this, the function 
     * author should insert a help comment at the beginning of their M code.  See the 
     * MATLAB documentation for more details.)
     *
     * @param nargout Number of outputs to return.
     * @param rhs The inputs to the M function.
     *
     * @return Array of length nargout containing the function outputs. Outputs are 
     * returned as sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>. 
     * Each output array should be freed by calling its <code>dispose()</code> method.
     *
     * @throws java.jmi.RemoteException An error has occurred during the function call or 
     * in communication with the server.
     */
    public Object[] prewhitening_1(int nargout, Object... rhs) throws RemoteException;
  
    /** Frees native resources associated with the remote server object */
    void dispose() throws RemoteException;
}
