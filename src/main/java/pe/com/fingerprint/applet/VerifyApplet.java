package pe.com.fingerprint.applet;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import pe.com.fingerprint.util.Constants;
import pe.com.fingerprint.util.FingerPrintAppletException;
import pe.com.fingerprint.util.ScannerUtil;
import pe.com.fingerprint.util.UFMatcher;
import pe.com.fingerprint.util.UFScanner;

/**
 * TODO comment!
 *
 * @author jcuevas
 * @version $Id$
 */
public class VerifyApplet
    extends JApplet
{
    public VerifyApplet() {
    }

    private UFScanner libScanner = null;
    private UFMatcher libMatcher = null;
    private ImagePanel imgPanel = null;
    private Integer nScannerNumber = 0;
    private Pointer hMatcher = null;
    private byte[] byteTemplateArray = null;
    private Integer intTemplateSizeArray = null;
    private byte[] byteTemplateStoredArray = null;
    private Integer intTemplateSizeStoredArray = null;
    private final int MAX_TEMPLATE_SIZE = 1024;
    private Integer callbackCount = 0;
    private Integer nInitFlag = 0;
    private Integer nCaptureFlag = 0;
    private JButton jBtnEnroll;
    private JButton jBtnSave;

    /**
     * Define el listener para la coneccion y desconeccion del scanner
     */
    UFScanner.UFS_SCANNER_PROC pScanProc = new UFScanner.UFS_SCANNER_PROC()
    {
        @Override
        public int callback(final String szScannerId,
                            final int bSensorOn,
                            final PointerByReference pParam) // interface ..must be implemeent
        {
            callbackCount++;
            System.out.println(callbackCount + "=========================================="); //
            System.out.println("==>ScanProc calle scannerID:" + szScannerId); //
            System.out.println("sensoron value is " + bSensorOn);
            System.out.println("void * pParam  value is " + pParam.getValue());
            System.out.println(callbackCount + "=========================================="); //

            ScannerUtil.updateScannerList(libScanner);
            return 1;
        }
    };

    /**
     * Define el listener para la captura de la huella.
     */
    UFScanner.UFS_CAPTURE_PROC pCaptureProc = new UFScanner.UFS_CAPTURE_PROC()
    {
        @Override
        public int callback(final Pointer hScanner,
                            final int bFingerOn,
                            final Pointer pImage,
                            final int nWidth,
                            final int nHeight,
                            final int nResolution,
                            final PointerByReference pParam)
        {
            callbackCount++;
            drawCurrentFingerImage();

            return 1;
        }
    };

    /**
     * Metodo para dibujar la imagen capturada en el panel.
     */
    public void drawCurrentFingerImage()
    {
        /* test draw image */
        final IntByReference refResolution = new IntByReference();
        final IntByReference refHeight = new IntByReference();
        final IntByReference refWidth = new IntByReference();
        Pointer hScanner = null;

        // Inicializa el manejador del scanner
        hScanner = ScannerUtil.getCurrentScannerHandle(libScanner);

        // Obtiene la informacion del buffer que captura la imagen
        libScanner.UFS_GetCaptureImageBufferInfo(hScanner, refWidth, refHeight, refResolution);

        final byte[] pImageData = new byte[refWidth.getValue() * refHeight.getValue()];

        // Se encarga de copiar la image capturada por el buffer hacia un array
        libScanner.UFS_GetCaptureImageBuffer(hScanner, pImageData);

        imgPanel.drawFingerImage(refWidth.getValue(), refHeight.getValue(), pImageData);
    }

    private void initScanners()
    {
        if (nInitFlag == 0) {
            nCaptureFlag = 0;
            try {
                libScanner = (UFScanner) Native.loadLibrary("UFScanner", UFScanner.class);
                libMatcher = (UFMatcher) Native.loadLibrary("UFMatcher", UFMatcher.class);

                int nRes = 0;
                nRes = libScanner.UFS_Init();
                if (nRes == 0) {
                    System.out.println("UFS_Init() success!!");
                    nInitFlag = 1;
                    nRes = ScannerUtil.testCallScanProcCallback(libScanner, pScanProc);
                    if (nRes == 0) {
                        // Una referencia a un entero para almacenar el numero del scaner
                        final IntByReference refNumber = new IntByReference();
                        // UFS_GetScannerNumber devuelve el numero del scanner en el entero de referencia
                        nRes = libScanner.UFS_GetScannerNumber(refNumber);
                        if (nRes == 0) {
                            nScannerNumber = refNumber.getValue();
                            // setStatusMsg("UFS_GetScannerNumber() scanner number :" + nScannerNumber);
                            final PointerByReference refMatcher = new PointerByReference();
                            // crea el matcher
                            nRes = libMatcher.UFM_Create(refMatcher);
                            if (nRes == 0) {
                                hMatcher = refMatcher.getValue();
                                // Obtiene la lista de scanners
                                ScannerUtil.updateScannerList(libScanner); // list upate ==>
                                System.out.println("after upadtelist");
                                ScannerUtil.initVariable(libScanner, libMatcher, hMatcher);
                                System.out.println("after initVariable");
                                initArray(); // array size,template size

                                final IntByReference refValue = new IntByReference();
                                final IntByReference refFastMode = new IntByReference();
                                // security level (1~7)
                                // 302 security level :UFM_PARAM_SECURITY_LEVEL
                                nRes = libMatcher.UFM_GetParameter(hMatcher, UFMatcher.UFM_PARAM_SECURITY_LEVEL,
                                                refValue);
                                if (nRes == 0) {
                                    System.out.println(
                                                    "Get security level,302(security) value is " + refValue.getValue());
                                } else {
                                    System.out.println("get security level fail! code: " + nRes);
                                }
                            } else {
                                System.out.println("UFM_Create fail!! code :" + nRes);
                                ScannerUtil.showErrorString(libScanner, nRes);
                                ScannerUtil.MsgBox(
                                                "Ocurrió un error inicializar librerías, por favor contacte al Administrador");
                            }

                        } else {
                            System.out.println("GetScannerNumber fail!! code :" + nRes);
                            ScannerUtil.showErrorString(libScanner, nRes);
                        }
                    } else {
                        System.out.println("UFS_SetScannerCallback() fail,code :" + nRes);
                        ScannerUtil.showErrorString(libScanner, nRes);
                    }
                } else {
                    System.out.println("Init() fail!!");
                    ScannerUtil.showErrorString(libScanner, nRes);
                    ScannerUtil.MsgBox("Ocurrió un error al cargar las librerías, por favor contacte al Administrador");
                }
            } catch (final Exception ex) {
                // setStatusMsg("loadLlibrary : UFScanner,UFMatcher fail!!");
                // MsgBox("loadLlibrary : UFScanner,UFMatcher fail!!");
                // return;
            }
        } else {
            System.out.println("Already Init");
            // return;
        }
    }

    public void initArray()
    {
        if (byteTemplateArray != null) {
            byteTemplateArray = null;
        }

        if (intTemplateSizeArray != null) {
            intTemplateSizeArray = null;
        }

        byteTemplateArray = new byte[MAX_TEMPLATE_SIZE];
        intTemplateSizeArray = 0;
    }

    private void loadStoredTemplate() {
        final String testParam = getParameter("testParam");
        System.out.println("TestParametro --> " + testParam);
        final String imageTemplateStr = getParameter("byteTemplateStoredArray");
        System.out.println("Template --> " + imageTemplateStr);
        this.byteTemplateStoredArray = Base64.decodeBase64(imageTemplateStr);
        this.intTemplateSizeStoredArray = this.byteTemplateStoredArray.length;
    }

    private void verifyFingerPrint() {

        Pointer hScanner = null;
        hScanner = ScannerUtil.getCurrentScannerHandle(libScanner);

        if (hScanner != null) {
            libScanner.UFS_ClearCaptureImageBuffer(hScanner);

            final IntByReference refVerify = new IntByReference();

            final int nRes = libMatcher.UFM_Verify(hMatcher, byteTemplateStoredArray, intTemplateSizeStoredArray,
                            byteTemplateArray, intTemplateSizeArray,
                            refVerify);// byte[][]
            if (nRes == 0) {
                try {
                    Thread.sleep(2000);
                } catch (final InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (refVerify.getValue() == 1) {
                    System.out.println("verify success!!");
                    sendResult(true);
                    //MsgBox("verify success!! enroll_id: " + (nSelectedIdx + 1));
                } else {
                    System.out.println("verify fail!!");
                    sendResult(false);
                    //MsgBox("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                }
            } else {
                System.out.println("Error al realizar el match");
                ScannerUtil.showErrorString(libScanner, nRes);
            }


        } else {
            System.out.println("getCurrentScannerHandle fail!! ");
            //return;
        }
    }

    private void sendResult(final Boolean _verify) {
        final byte[] fileId = getClienteIdFile();
        final String strId = Base64.encodeBase64String(fileId);
        try {
            final JSObject jso = JSObject.getWindow(this);
            jso.call("notifyServer", _verify, strId);
            System.out.println("notifyServer Fired!");
        } catch(final JSException ex) {
            System.out.println("Could not create JS Object. Javascript Disabled!");
        }
    }

    private byte[] getClienteIdFile()
    {
        final String path = Constants.SYSTEM_STORE_IDPATH + File.separator + "store";
        final File file = new File(path);
        byte[] ret;
        try {
            final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n;
            while ((n = bis.read()) != -1) {
                baos.write(n);
            }
            ret = baos.toByteArray();
            bis.close();
            baos.close();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return ret;
    }

    class ImagePanel
        extends JPanel
    {

        // private PlanarImage image;
        private BufferedImage buffImage = null;

        private void drawFingerImage(final int nWidth,
                                     final int nHeight,
                                     final byte[] buff)
        {
            buffImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_BYTE_GRAY);
            buffImage.getRaster().setDataElements(0, 0, nWidth, nHeight, buff);

            final Graphics g = buffImage.createGraphics();
            // g.drawImage(buffImage, 0, 0, nWidth, nHeight, null);
            g.drawImage(buffImage, 0, 0, imgPanel.getWidth(), imgPanel.getHeight(), imgPanel);
            g.dispose();
            repaint();
        }

        @Override
        public void paintComponent(final Graphics g)
        {
            g.drawImage(buffImage, 0, 0, this);
        }

    }

    /*
     * (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        super.init();
        if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.setProperty("jna.library.path", Constants.SYSTEM_PATH_64);
        } else if (System.getProperty("sun.arch.data.model").equals("32")) {
            System.setProperty("jna.library.path", Constants.SYSTEM_PATH_32);
        } else {
            System.setProperty("jna.library.path", Constants.SYSTEM_PATH_32);
        }

        try {
            getJContentPane();
            if (libScanner == null) {
                libScanner = (UFScanner) Native.loadLibrary("UFScanner", UFScanner.class);
                libMatcher = (UFMatcher) Native.loadLibrary("UFMatcher", UFMatcher.class);
                int nRes = libScanner.UFS_Uninit();
                if (nRes == 0) {
                    System.out.println("UFS_Uninit sucess 1!!");
                    nRes = libMatcher.UFM_Delete(hMatcher);
                    nInitFlag = 0;
                } else {
                    System.out.println("UFS_Uninit fail 1!!");
                }
                libScanner = null;
                libMatcher = null;
                loadStoredTemplate();
                initScanners();
            }
        } catch (final FingerPrintAppletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.applet.Applet#start()
     */
    @Override
    public void start()
    {
        // TODO Auto-generated method stub
        super.start();
    }

    /*
     * (non-Javadoc)
     * @see java.applet.Applet#stop()
     */
    @Override
    public void stop()
    {
        // TODO Auto-generated method stub
        super.stop();
    }

    /*
     * (non-Javadoc)
     * @see java.applet.Applet#destroy()
     */
    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        super.destroy();
    }

    private ImagePanel getImagePanel()
    {
        if (imgPanel == null) {
            imgPanel = new ImagePanel();
            imgPanel.setLayout(null);
            imgPanel.setBounds(new Rectangle(0, 0, 200, 300));

        }
        return imgPanel;
    }

    /**
     * This method initializes jBtnEnroll
     *
     * @return javax.swing.JButton
     */
    private JButton getJBtnEnroll()
        throws FingerPrintAppletException
    {
        if (jBtnEnroll == null) {
            jBtnEnroll = new JButton();
            jBtnEnroll.setBounds(new Rectangle(0, 300, 200, 25));
            jBtnEnroll.setText("Capturar Huella");
            jBtnEnroll.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag != 0) {
                        Pointer hScanner = null;
                        hScanner = ScannerUtil.getCurrentScannerHandle(libScanner);
                        if (hScanner != null) {
                            int nRes = libScanner.UFS_ClearCaptureImageBuffer(hScanner);
                            System.out.println("place a finger");
                            try {
                                Thread.sleep(1000);
                            } catch (final InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            nRes = ScannerUtil.callStartCapturing(libScanner, hScanner, pCaptureProc);
                            System.out.println("capture single image");
                            if (nRes == 0) {
                                final byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
                                final IntByReference refTemplateSize = new IntByReference();
                                final IntByReference refTemplateQuality = new IntByReference();
                                // Es necesario un sleep para que termine el scanneo antes de comenzar a extraer.
                                try {
                                    Thread.sleep(800);
                                } catch (final InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                for (int i=0; i<500000; i++) {}
                                nRes = libScanner.UFS_ExtractEx(hScanner, MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize,
                                                refTemplateQuality);
                                if (nRes == 0) {
                                    System.out.println("save template file template size:" + refTemplateSize.getValue()
                                                    + " quality:" + refTemplateQuality.getValue());

                                    if (refTemplateQuality.getValue() < Constants.UFS_PARAM_QUALITY_5) {
                                        ScannerUtil.MsgBox("Calidad de la huella dactilar demasiado baja < "
                                                            + Constants.UFS_PARAM_QUALITY_5);
                                    } else {
                                        final int tempsize = refTemplateSize.getValue();

                                        System.arraycopy(bTemplate, 0, byteTemplateArray, 0,
                                                        refTemplateSize.getValue());// byte[][]

                                        intTemplateSizeArray = refTemplateSize.getValue();

                                        System.out.println("enroll template array idx:"
                                                        + " template size:"
                                                        + intTemplateSizeArray);

                                        //drawCurrentFingerImage();

                                        nCaptureFlag = 1;

                                        verifyFingerPrint();
                                    }
                                } else {
                                    System.out.println("Enroll Image fail!! code:" + nRes);
                                    final byte[] refErr = new byte[512];
                                    nRes = libScanner.UFS_GetErrorString(nRes, refErr);
                                    if (nRes == 0) {
                                        System.out.println("==>UFS_GetErrorString err is "
                                                        + Native.toString(refErr));
                                    }
                                    ScannerUtil.MsgBox("Por favor vuelva a realizar la captura de su huella dactilar");
                                }

                            }
                        } else {
                            // scanner pointer null
                        }
                    } else {
                        ScannerUtil.MsgBox("Scanner no inicializado, recargar la página!");
                        // return;
                    }
                }
            });

        }
        return jBtnEnroll;
    }

    private JButton getJBtnVerify() {
        if (jBtnSave == null) {
            jBtnSave = new JButton("Verificar");
            jBtnSave.setBounds(new Rectangle(100, 300, 100, 25));
            jBtnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    verifyFingerPrint();
                }
            });
        }
        return jBtnSave;
    }

    private void getJContentPane()
        throws FingerPrintAppletException
    {
        this.setSize(200, 325);
        this.setLayout(null);
        getContentPane().add(getJBtnEnroll());
        getContentPane().add(getImagePanel());
        //getContentPane().add(getJBtnVerify());
    }
}
