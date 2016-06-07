package pe.com.fingerprint.applet;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JPanel;

import pe.com.fingerprint.util.ScannerUtil;
import pe.com.fingerprint.util.UFMatcher;
import pe.com.fingerprint.util.UFScanner;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * TODO comment!
 *
 * @author jcuevas
 * @version $Id$
 */
public class RegisterApplet
    extends JApplet
{
    private UFScanner libScanner = null;
    private UFMatcher libMatcher = null;
    private final Image fingerImg = null;
    private ImagePanel imgPanel = null;
    private int nScannerNumber = 0;
    private Pointer hMatcher = null;
    private PointerByReference refTemplateArray = null; // @jve:decl-index=0:
    private final Pointer[] pArray = null;
    private final String[] strTemplateArray = null;
    private byte[][] byteTemplateArray = null;
    private int[] intTemplateSizeArray = null;
    private final int MAX_TEMPLATE_SIZE = 1024;
    private Integer callbackCount = 0;
    private Integer nInitFlag = 0;
    private Integer nCaptureFlag = 0;
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

            ScannerUtil.UpdateScannerList(libScanner);
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
        hScanner = ScannerUtil.GetCurrentScannerHandle(libScanner);

        // Obtiene la informacion del buffer que captura la imagen
        libScanner.UFS_GetCaptureImageBufferInfo(hScanner, refWidth, refHeight, refResolution);

        final byte[] pImageData = new byte[refWidth.getValue() * refHeight.getValue()];

        // Se encarga de copiar la image capturada por el buffer hacia un array
        libScanner.UFS_GetCaptureImageBuffer(hScanner, pImageData);

        imgPanel.drawFingerImage(refWidth.getValue(), refHeight.getValue(), pImageData);
    }

    private void initScanners() {
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
                    //getJTextField_status().setText("UFS_Init() success,nInitFlag value set 1");
                    //MsgBox("Scanner Init success!!");
                    nRes = ScannerUtil.testCallScanProcCallback(libScanner, pScanProc);
                    if (nRes == 0) {
                        //setStatusMsg("==>UFS_SetScannerCallback pScanProc ...");
                        // Una referencia a un entero para almacenar el numero del scaner
                        final IntByReference refNumber = new IntByReference();
                        // UFS_GetScannerNumber devuelve el numero del scanner en el entero de referencia
                        nRes = libScanner.UFS_GetScannerNumber(refNumber);
                        if (nRes == 0) {
                            nScannerNumber = refNumber.getValue();
                            //setStatusMsg("UFS_GetScannerNumber() scanner number :" + nScannerNumber);
                            final PointerByReference refMatcher = new PointerByReference();
                            // crea el matcher
                            nRes = libMatcher.UFM_Create(refMatcher);
                            if (nRes == 0) {
                                hMatcher = refMatcher.getValue();
                                // Obtiene la lista de scanners
                                ScannerUtil.UpdateScannerList(libScanner); // list upate ==>
                                System.out.println("after upadtelist");
                                ScannerUtil.initVariable(libScanner, libMatcher, hMatcher);
                                System.out.println("after initVariable");
                                initArray(100, 1024); // array size,template
                                                      // size


                                final IntByReference refValue = new IntByReference();
                                final IntByReference refFastMode = new IntByReference();
                                // security level (1~7)
                                // 302 security level :UFM_PARAM_SECURITY_LEVEL
                                nRes = libMatcher.UFM_GetParameter(hMatcher, UFMatcher.UFM_PARAM_SECURITY_LEVEL, refValue);
                                if (nRes == 0) {
                                    System.out.println("Get security level,302(security) value is " + refValue.getValue());
                                } else {
                                    System.out.println("get security level fail! code: " + nRes);
                                }
                            } else {
                                System.out.println("UFM_Create fail!! code :" + nRes);
                                // return;
                            }

                        } else {
                            System.out.println("GetScannerNumber fail!! code :" + nRes);
                            // setStatusMsg("GetScannerNumber fail!! code :" + nRes);
                            // return;
                        }
                    } else {
                        System.out.println("UFS_SetScannerCallback() fail,code :" + nRes);
                    }
                }
                if (nRes != 0) {
                    System.out.println("Init() fail!!");
                    //setStatusMsg("Init fail!! return code:" + nRes);
                    //MsgBox("Scanner Init fail!!");
                }
            } catch (final Exception ex) {
                //setStatusMsg("loadLlibrary : UFScanner,UFMatcher fail!!");
                //MsgBox("loadLlibrary : UFScanner,UFMatcher fail!!");
                // return;
            }
        } else {
            System.out.println("Already Init");
            // return;
        }
    }

    public void initArray(final int nArrayCnt,
                          final int nMaxTemplateSize)
    {
        if (byteTemplateArray != null) {
            byteTemplateArray = null;
        }

        if (intTemplateSizeArray != null) {
            intTemplateSizeArray = null;
        }

        byteTemplateArray = new byte[nArrayCnt][MAX_TEMPLATE_SIZE];
        intTemplateSizeArray = new int[nArrayCnt];
        refTemplateArray = new PointerByReference();
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
        getJContentPane();
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
        initScanners();
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
            imgPanel.setBounds(new Rectangle(260, 17, 270, 310));

        }
        return imgPanel;
    }

    private void getJContentPane(){
        final BorderLayout bl = new BorderLayout();
        this.setLayout(bl);
        this.add(getImagePanel(), BorderLayout.CENTER);
    }
}
