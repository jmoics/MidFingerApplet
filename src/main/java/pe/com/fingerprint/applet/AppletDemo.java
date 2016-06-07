package pe.com.fingerprint.applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import pe.com.fingerprint.util.UFMatcher;
import pe.com.fingerprint.util.UFScanner;

public class AppletDemo
    extends JApplet
{
    private JPanel jContentPane = null;
    private JButton jButton_ufe_init = null;
    private JList jList = null;
    private final JOptionPane jOptionPane = null;
    private final JButton jButton_caputure_single = null;
    private UFScanner libScanner = null;
    private UFMatcher libMatcher = null;
    private JTextField jTextField_status = null;
    private JButton jButton_update = null;
    private JButton jButton_Uninit = null;
    private JList jList1_scanner_list = null;
    private JComboBox jComboBox_timeout = null;
    private JLabel jLabel_timeout = null;
    private JLabel jLabel_brightness = null;
    private JLabel jLabel_sense = null;
    private JButton jButton_start_capturing = null;
    private JButton jButton_abort = null;
    private JButton jButton_extractor = null;
    private JLabel jLabel_enroll = null;
    private JLabel jLabel_detect_fake = null;
    private JComboBox jComboBox_detect_fake = null;
    private JComboBox jComboBox_enroll = null;
    private JLabel jLabel_parameter = null;
    private final JPanel jContentPane1 = null;
    private JList jList_msg_log = null;
    private JScrollPane listLogScrollPane = null;
    private JLabel jLabel_sense1 = null;
    private JLabel jLabel_match = null;
    private JLabel jLabel_security_levle = null;
    private JComboBox jComboBox_security_level = null;
    private JCheckBox jCheckBox_fastmode = null;
    private JLabel jLabel_enrollid = null;
    private JComboBox jComboBox_enrollid = null;
    private JButton jButton_verity = null;
    private JButton jButton_Identify = null;
    private JButton jButton_enroll = null;
    private JButton jButton_save_tmp = null;
    private JButton jButton_save_img = null;
    private JLabel jLabel_scanner_list = null;
    private JButton jButton_clear = null;
    public Image fingerImg = null;
    private ImagePanel imgPanel = null;
    private int nScannerNumber = 0;
    private DefaultListModel listModel; // @jve:decl-index=0:visual-constraint="518,17"
    private DefaultListModel listLogModel; // @jve:decl-index=0:visual-constraint="507,84"
    private JScrollPane listScrollPane = null;
    private final JScrollPane logScrollPane = null;
    public int nC = 0;
    private int nInitFlag = 0;
    private int nCaptureFlag = 0;
    private byte[][] byteTemplateArray = null;
    private int[] intTemplateSizeArray = null;
    private int nTemplateCnt = 0;
    private int nLogListCnt = 0;
    private Pointer hMatcher = null;
    private PointerByReference refTemplateArray = null; // @jve:decl-index=0:
    Pointer[] pArray = null;
    private final String[] strTemplateArray = null;
    private int nSecurityLevel = 0;
    private final int nDetectFake = 2;
    private int nFastMode = 0;
    private JComboBox jComboBox_bri = null;
    private JComboBox jComboBox_sens = null;
    private JTextField jFingerInfo = null;
    private final JList jList1 = null;
    private JComboBox jComboBox_ScanType = null;
    private JComboBox jComboBox_MatchType = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    public final int MAX_TEMPLATE_SIZE = 2048;

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
            nC++;
            System.out.println(nC + "=========================================="); //
            System.out.println("==>ScanProc calle scannerID:" + szScannerId); //
            System.out.println("sensoron value is " + bSensorOn);
            System.out.println("void * pParam  value is " + pParam.getValue());
            System.out.println(nC + "=========================================="); //

            UpdateScannerList();
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
            nC++;
            /*
             * System.out.println(nC+
             * "=========================================="); //
             * System.out.println("==>captureProc calle scanner:"+hScanner); //
             * System.out.println(" fingerOn:"+bFingerOn); //
             * System.out.println("width: "+nWidth); System.out.println(
             * "height: "+nHeight); System.out.println("resolution: "
             * +nResolution); System.out.println("void * pParam  value is "
             * +pParam.getValue()); System.out.println(nC+
             * "=========================================="); //
             */
            drawCurrentFingerImage();

            /*
             * jFingerInfo.setText(""); jFingerInfo.setText("width:"+nWidth +
             * " height:"+nHeight+" resolution:"+nResolution);
             */

            // MsgBox("call"+nC); //exception error==> SDK work
            // thread(UFS_Capture_Thread) while loop �� try ,catch
            return 1;

        }
    };

    public void MsgBox(final String log)
    {
        JOptionPane.showMessageDialog(null, log);
    }

    public void setStatusMsg(String log)
    {
        getJTextField_status().setText("");
        getJTextField_status().setText(log);

        log = nLogListCnt + ":" + log;
        listLogModel.insertElementAt(log, nLogListCnt);
        nLogListCnt++;
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

    /**
     * Inicializa los valores de los combos
     * @param nFlag
     */
    public void initVariable(final int nFlag)
    {
        if (nFlag == 1) { // UFS_Init\//
            nInitFlag = 1;
        } else {
            nInitFlag = 0;
        }

        nCaptureFlag = 0;
        nLogListCnt = 0;
        listLogModel.clear();

        String szComboItem = null;
        for (int i = 0; i < 6; i++) {
            szComboItem = String.valueOf(i);
            jComboBox_timeout.insertItemAt(szComboItem, i);
        }
        jComboBox_timeout.setSelectedIndex(0);

        ///////////////////////////////////////////////////
        szComboItem = null;
        for (int i = 0; i < 256; i++) {
            szComboItem = String.valueOf(i);
            jComboBox_bri.insertItemAt(szComboItem, i);
        }
        jComboBox_bri.setSelectedIndex(100); // 100

        /////////////////////////////////////////////////
        szComboItem = null;
        for (int i = 0; i < 8; i++) {
            szComboItem = String.valueOf(i);
            jComboBox_sens.insertItemAt(szComboItem, i);
        }
        jComboBox_sens.setSelectedIndex(4); // 4

        /////////////////////////////////////////////////
        szComboItem = null;
        for (int i = 0; i < 6; i++) {
            szComboItem = String.valueOf((i * 10));
            jComboBox_enroll.insertItemAt(szComboItem, i);
        }
        jComboBox_enroll.setSelectedIndex(5);

        szComboItem = null;
        for (int i = 0; i < 4; i++) {
            szComboItem = String.valueOf(i);
            jComboBox_detect_fake.insertItemAt(szComboItem, i);
        }
        jComboBox_detect_fake.setSelectedIndex(2);

        ///////////////////////////////////////////////
        jComboBox_ScanType.insertItemAt("suprema*", 0);
        jComboBox_ScanType.insertItemAt("iso_19794_2", 1);
        jComboBox_ScanType.insertItemAt("ansi378", 2);
        jComboBox_ScanType.setSelectedIndex(0);
        ///////////////////////////////////////////////
        jComboBox_MatchType.insertItemAt("suprema*", 0);
        jComboBox_MatchType.insertItemAt("iso_19794_2", 1);
        jComboBox_MatchType.insertItemAt("ansi378", 2);
        jComboBox_MatchType.setSelectedIndex(0);
        ///////////////////////////////////////////////
        szComboItem = null;
        int nTempRate = 10;
        for (int i = 1; i < 8; i++) {
            szComboItem = String.valueOf(i);
            if (i == 1) {
                szComboItem = szComboItem + "(FAR " + 1 + "/" + nTempRate * 10 + ")";
            } else {
                szComboItem = szComboItem + "(" + 1 + "/" + nTempRate * 10 + ")";
            }
            jComboBox_security_level.insertItemAt(szComboItem, i - 1);
            nTempRate = nTempRate * 10;
        }
        jComboBox_security_level.setSelectedIndex(4); // 7

        Pointer hScanner = null;
        hScanner = GetCurrentScannerHandle();

        final IntByReference pValue = new IntByReference();

        pValue.setValue(5000);

        int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_TIMEOUT, pValue);
        if (nRes == 0) {
            setStatusMsg("Change combox-timeout,201(timeout) value is " + pValue.getValue());
        } else {
            setStatusMsg("Change combox-timeout,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(100);
        nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_BRIGHTNESS, pValue);
        if (nRes == 0) {
            setStatusMsg("Change combox-brightness,202 value is " + pValue.getValue());
        } else {
            setStatusMsg("Change combox-brightness,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(2);
        nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_DETECT_FAKE, pValue);
        if (nRes == 0) {
            setStatusMsg("Change combox-detect_fake,312(fake detect) value is " + pValue.getValue());
        } else {
            setStatusMsg("Change combox-detect_fake,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(4);
        nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_SENSITIVITY, pValue);
        if (nRes == 0) {
            setStatusMsg("Change combox-sensitivity,203 value is " + pValue.getValue());
        } else {
            setStatusMsg("Change combox-sensitivity,change parameter value fail! code: " + nRes);
        }

        nRes = libScanner.UFS_SetTemplateType(hScanner, libScanner.UFS_TEMPLATE_TYPE_SUPREMA); // 2001
                                                                                               // Suprema
                                                                                               // type
        if (nRes == 0) {
            setStatusMsg("Change combox-Scan TemplateType:2001");
        } else {
            setStatusMsg("Change combox-Scan TemplateType,change parameter value fail! code: " + nRes);
        }
    }

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
        hScanner = GetCurrentScannerHandle();

        // Obtiene la informacion del buffer que captura la imagen
        libScanner.UFS_GetCaptureImageBufferInfo(hScanner, refWidth, refHeight, refResolution);

        final byte[] pImageData = new byte[refWidth.getValue() * refHeight.getValue()];

        // Se encarga de copiar la image capturada por el buffer hacia un array
        libScanner.UFS_GetCaptureImageBuffer(hScanner, pImageData);

        imgPanel.drawFingerImage(refWidth.getValue(), refHeight.getValue(), pImageData);
    }

    /**
     * Especie de listener que detecta cuando se conecta o se desconecta el dispositivo scanner (luego del Init).
     */
    public void UpdateScannerList()
    {
        int nSelectedIdx = 0;
        int nRes = 0;
        final PointerByReference hTempScanner = new PointerByReference();
        Pointer hScanner = null;
        final IntByReference refType = new IntByReference();

        final byte[] bScannerId = new byte[512];

        String szListLog = null;
        int nNumber = 0;

        System.out.println("==update Scanner list==");

        final IntByReference refNumber = new IntByReference();
        nRes = libScanner.UFS_GetScannerNumber(refNumber);

        if (nRes == 0) {
            System.out.println("UFS_GetScannerNumber() res value is " + nRes);
            nNumber = refNumber.getValue();

            if (nNumber <= 0) {
                if (listModel.getSize() > 0) {
                    listModel.clear();
                    System.out.println("list clear");
                }
            } else {
                if (listModel.getSize() > 0) {
                    listModel.clear();
                    System.out.println("list clear");
                }

                for (int j = 0; j < nNumber; j++) {
                    nRes = libScanner.UFS_GetScannerHandle(j, hTempScanner);
                    hScanner = null;
                    hScanner = hTempScanner.getValue();

                    if (nRes == 0 && hScanner != null) {
                        nRes = libScanner.UFS_GetScannerID(hScanner, bScannerId);
                        nRes = libScanner.UFS_GetScannerType(hScanner, refType);
                        szListLog = "ID : " + Native.toString(bScannerId) + " Type : " + refType.getValue();
                        listModel.insertElementAt(szListLog, j);

                    }
                }

                // al inicializar el jContentPane se agrego listModel (vacio) al jList1_scanner_list
                nSelectedIdx = jList1_scanner_list.getSelectedIndex();
                if (nSelectedIdx == -1) {
                    nSelectedIdx = 0;
                }

                jList1_scanner_list.setSelectedIndex(nSelectedIdx);
                jList1_scanner_list.ensureIndexIsVisible(nSelectedIdx);
            }
        }
    }

    public int testCallScanProcCallback()
    {
        int nRes = 0;

        final PointerByReference refParam = new PointerByReference();
        refParam.getPointer().setInt(0, 1);

        nRes = libScanner.UFS_SetScannerCallback(pScanProc, refParam);
        if (nRes == 0) {
            System.out.println("==>UFS_SetScannerCallback pScanProc ..." + pScanProc);
        }
        return nRes;
    }

    /**
     * Inicia el listener para captura de la imagen.
     * @return
     */
    public int testCallStartCapturing(final Pointer hScanner)
    {
        int nRes = 0;
        final PointerByReference refParam = new PointerByReference();

        if (hScanner != null) {
            System.out.println("UFS_StartCapturing,get current scanner handle success! : " + hScanner);
            setStatusMsg("get Scanner handle success pointer:" + hScanner);

            // inicia el listener de captura de huella para que pueda inicializarse y capturar la imagen
            nRes = libScanner.UFS_StartCapturing(hScanner, pCaptureProc, refParam);

            if (nRes == 0) {
                setStatusMsg("UFS_StartCapturing success!!");
                System.out.println("UFS_StartCapturing success!!");
                nCaptureFlag = 1;
            } else {
                setStatusMsg("UFS_StartCapturing fail!! code:" + nRes);
            }
        } else {
            System.out.println("UFS_StartCapturing,GetScannerHandle fail!!");
            setStatusMsg("UFS_StartCapturing,get Scanner handle fail!!");
        }
        return nRes;
    }

    /**
     * Obtiene el manejador del scanner utilizando el indice que lo identifica en memoria (detectado en UpdateScannerList)
     * @return
     */
    public Pointer GetCurrentScannerHandle()
    {
        Pointer hScanner = null;
        int nRes = 0;
        int nNumber = 0;

        final PointerByReference refScanner = new PointerByReference();
        final IntByReference refScannerNumber = new IntByReference();

        // success!!//
        nRes = libScanner.UFS_GetScannerNumber(refScannerNumber);

        if (nRes == 0) {
            nNumber = refScannerNumber.getValue();
            if (nNumber > 0) {
                int index = jList1_scanner_list.getSelectedIndex();
                if (index == -1) {
                    index = 0;
                } else {

                }

                jList1_scanner_list.setSelectedIndex(index);
                jList1_scanner_list.ensureIndexIsVisible(index);

                nRes = libScanner.UFS_GetScannerHandle(index, refScanner);
                hScanner = refScanner.getValue();

                /*if (nRes == 0 && hScanner != null) {
                    return hScanner;
                }*/
            }
        }

        return hScanner;
    }

    public void getCurrentScannerInfo()
    {
        /*
         * PARAMETER value for scanner 201 : timeout 202 : brightness 203 :
         * sensitivity 204 : serial for extracting 301 : detect core 302 :
         * template size 311 : use sif
         */

        Pointer hScanner = null;
        int nRes = 0;
        int nNumber = 0;

        final PointerByReference refScanner = new PointerByReference();
        final IntByReference refScannerNumber = new IntByReference();

        nRes = libScanner.UFS_GetScannerNumber(refScannerNumber);
        if (nRes == 0) {
            nNumber = refScannerNumber.getValue();
            if (nNumber > 0) {
                int index = jList1_scanner_list.getSelectedIndex();
                if (index == -1) {
                    index = 0;
                } else {

                }

                jList1_scanner_list.setSelectedIndex(index);
                jList1_scanner_list.ensureIndexIsVisible(index);

                nRes = libScanner.UFS_GetScannerHandle(index, refScanner);

                hScanner = refScanner.getValue();
                if (nRes == 0 && hScanner != null) {
                    // getParameter
                    final IntByReference pValue = new IntByReference();
                    int nSelectedIdx = 0;
                    nRes = libScanner.UFS_GetParameter(hScanner, 201, pValue);
                    System.out.println("===>UFS_GetParameter ,201(timeout) value is " + pValue.getValue());
                    if (nRes == 0 && jComboBox_timeout.getItemCount() > 0) {
                        for (int i = 0; i < 6; i++) {
                            nSelectedIdx = Integer.parseInt((String) (jComboBox_timeout.getItemAt(i)));
                            if (nSelectedIdx == pValue.getValue() / 1000) {
                                jComboBox_timeout.setSelectedIndex(i);
                                break;
                            }
                        }
                    }

                    nRes = libScanner.UFS_GetParameter(hScanner, 202, pValue);
                    System.out.println("===>UFS_GetParameter ,202(brightness) value is " + pValue.getValue());

                    if (nRes == 0 && jComboBox_bri.getItemCount() > 0) {
                        for (int i = 0; i < 256; i++) {
                            nSelectedIdx = Integer.parseInt((String) (jComboBox_bri.getItemAt(i)));
                            if (nSelectedIdx == pValue.getValue()) {
                                jComboBox_bri.setSelectedIndex(i);
                                break;
                            }
                        }
                    }

                    nRes = libScanner.UFS_GetParameter(hScanner, 312, pValue);
                    System.out.println("===>UFS_GetParameter ,312(detect_fake) value is " + pValue.getValue());

                    if (nRes == 0 && jComboBox_detect_fake.getItemCount() > 0) {
                        for (int i = 0; i < 4; i++) {
                            nSelectedIdx = Integer.parseInt((String) (jComboBox_detect_fake.getItemAt(i)));
                            if (nSelectedIdx == pValue.getValue()) {
                                jComboBox_detect_fake.setSelectedIndex(i);
                                break;
                            }
                        }
                    }

                    nRes = libScanner.UFS_GetParameter(hScanner, 203, pValue);
                    System.out.println("===>UFS_GetParameter ,203(sensitivity) value is " + pValue.getValue());

                    if (nRes == 0 && jComboBox_sens.getItemCount() > 0) {
                        for (int i = 0; i < 8; i++) {
                            nSelectedIdx = Integer.parseInt((String) (jComboBox_sens.getItemAt(i)));
                            if (nSelectedIdx == pValue.getValue()) {
                                jComboBox_sens.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }

            } else {
                //return;
                System.out.println("No hay scanner");
            }
        } else {
            //return;
        }


        return;
    }

    /**
     * Inicializa el proceso de captura de huellas.
     * @return
     */
    private JButton getJButton_ufe_init()
    {
        if (jButton_ufe_init == null) {
            jButton_ufe_init = new JButton();
            jButton_ufe_init.setBounds(new Rectangle(15, 15, 65, 21));
            jButton_ufe_init.setText("Init");

            jButton_ufe_init.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag != 0) {
                        MsgBox("already init..");
                        // return;
                    } else {
                        nCaptureFlag = 0;
                        try {
                            libScanner = (UFScanner) Native.loadLibrary("UFScanner", UFScanner.class);
                            libMatcher = (UFMatcher) Native.loadLibrary("UFMatcher", UFMatcher.class);
                            //loadLicense("UFLicense");

                            int nRes = 0;
                            nRes = libScanner.UFS_Init();

                            if (nRes == 0) {
                                System.out.println("UFS_Init() success!!");
                                nInitFlag = 1;
                                getJTextField_status().setText("UFS_Init() success,nInitFlag value set 1");
                                MsgBox("Scanner Init success!!");
                                nRes = testCallScanProcCallback();
                                if (nRes == 0) {
                                    setStatusMsg("==>UFS_SetScannerCallback pScanProc ...");
                                    // Una referencia a un entero para almacenar el numero del scaner
                                    final IntByReference refNumber = new IntByReference();
                                    // UFS_GetScannerNumber devuelve el numero del scanner en el entero de referencia
                                    nRes = libScanner.UFS_GetScannerNumber(refNumber);
                                    if (nRes == 0) {
                                        nScannerNumber = refNumber.getValue();
                                        setStatusMsg("UFS_GetScannerNumber() scanner number :" + nScannerNumber);
                                        final PointerByReference refMatcher = new PointerByReference();
                                        // crea el matcher
                                        nRes = libMatcher.UFM_Create(refMatcher);
                                        if (nRes == 0) {
                                            // Obtiene la lista de scanners
                                            UpdateScannerList(); // list upate ==>
                                                                 // getcurrentscannerhandle
                                                                 // list�� ������//
                                            System.out.println("after upadtelist");
                                            initVariable(1);
                                            System.out.println("after initVariable");
                                            initArray(100, 1024); // array size,template
                                                                  // size

                                            hMatcher = refMatcher.getValue();

                                            final IntByReference refValue = new IntByReference();
                                            final IntByReference refFastMode = new IntByReference();
                                            // security level (1~7)
                                            nRes = libMatcher.UFM_GetParameter(hMatcher, 302, refValue); // 302
                                                                                                         // :
                                                                                                         // security
                                                                                                         // level
                                                                                                         // :UFM_
                                            if (nRes == 0) {
                                                nSecurityLevel = refValue.getValue();//
                                                setStatusMsg("get security level,302(security) value is "
                                                                + refValue.getValue());
                                            } else {
                                                setStatusMsg("get security level fail! code: " + nRes);
                                                MsgBox("get security level fail! code: " + nRes);
                                            }

                                            if (nRes == 0 && jComboBox_security_level.getItemCount() > 0) {
                                                for (int i = 0; i < 6; i++) {

                                                    if (i == refValue.getValue() - 1) { // i
                                                                                        // :
                                                                                        // list
                                                                                        // index(zero
                                                                                        // based)
                                                                                        // ,refValue
                                                        jComboBox_security_level.setSelectedIndex(i);
                                                        break;
                                                    }
                                                }
                                            }

                                            // fast mode
                                            nRes = libMatcher.UFM_SetParameter(hMatcher, libMatcher.UFM_PARAM_FAST_MODE,
                                                            refFastMode);
                                            if (nRes == 0) {
                                                nFastMode = refFastMode.getValue();
                                                setStatusMsg("get fastmode,301(fastmode) value is " + refFastMode.getValue());
                                                // MsgBox("get fastmode,301(fastmode)
                                                // value is "+refFastMode.getValue());
                                            } else {
                                                setStatusMsg("get fastmode value fail! code: " + nRes);
                                                MsgBox("get fastmode value fail! code: " + nRes);
                                            }
                                            if (nFastMode == 1) {
                                                jCheckBox_fastmode.setSelected(true);
                                            }

                                            final int nSelectedIdx = jComboBox_MatchType.getSelectedIndex();

                                            if (hMatcher != null) {
                                                switch (nSelectedIdx) {
                                                    case 0:
                                                        nRes = libMatcher.UFM_SetTemplateType(hMatcher,
                                                                        libMatcher.UFM_TEMPLATE_TYPE_SUPREMA); // 2001
                                                                                                               // Suprema
                                                                                                               // type
                                                        break;
                                                    case 1:
                                                        nRes = libMatcher.UFM_SetTemplateType(hMatcher,
                                                                        libMatcher.UFM_TEMPLATE_TYPE_ISO19794_2); // 2002
                                                                                                                  // iso
                                                                                                                  // type
                                                        break;
                                                    case 2:
                                                        nRes = libMatcher.UFM_SetTemplateType(hMatcher,
                                                                        libMatcher.UFM_TEMPLATE_TYPE_ANSI378); // 2003
                                                                                                               // ansi
                                                                                                               // type
                                                        break;
                                                }
                                            }

                                        } else {
                                            setStatusMsg("UFM_Create fail!! code :" + nRes);
                                            // return;
                                        }

                                    } else {
                                        MsgBox("GetScannerNumber fail!! code :" + nRes);
                                        setStatusMsg("GetScannerNumber fail!! code :" + nRes);
                                        // return;
                                    }
                                } else {
                                    setStatusMsg("UFS_SetScannerCallback() fail,code :" + nRes);
                                }
                            }
                            if (nRes != 0) {
                                System.out.println("Init() fail!!");
                                setStatusMsg("Init fail!! return code:" + nRes);
                                MsgBox("Scanner Init fail!!");
                            }
                        } catch (final Exception ex) {
                            setStatusMsg("loadLlibrary : UFScanner,UFMatcher fail!!");
                            MsgBox("loadLlibrary : UFScanner,UFMatcher fail!!");
                            // return;
                        }
                    }
                }
            });

        }
        return jButton_ufe_init;
    }

    private void loadLicense(final String name)
    {
        final long handle = 0;
        final String libraryPath = null;
        try {
            final File embedded = extractFromResourcePath(name, AppletDemo.class.getClassLoader());
        }
        catch(final IOException e2) {
            new UnsatisfiedLinkError(e2.getMessage());
        }
    }

    public static File extractFromResourcePath(final String name, ClassLoader loader) throws IOException {
        final boolean DEBUG = true;
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
            // Context class loader is not guaranteed to be set
            if (loader == null) {
                loader = Native.class.getClassLoader();
            }
        }
        if (DEBUG) {
            System.out.println("Looking in classpath from " + loader + " for " + name);
        }
        final String libname = name + ".dat";
        String resourcePath = name.startsWith("/") ? name : Platform.RESOURCE_PREFIX + "/" + libname;
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        if (DEBUG) {
            System.out.println("Resource Path = " + resourcePath);
        }
        URL url = loader.getResource(resourcePath);
        if (DEBUG) {
            System.out.println("URL loader.getResource(resourcePath) =  " + url);
        }
        if (url == null && resourcePath.startsWith(Platform.RESOURCE_PREFIX)) {
            // If not found with the standard resource prefix, try without it
            url = loader.getResource(libname);
        }
        if (DEBUG) {
            System.out.println("URL loader.getResource(libname) =  " + url);
        }
        if (url == null) {
            String path = System.getProperty("java.class.path");
            if (loader instanceof URLClassLoader) {
                path = Arrays.asList(((URLClassLoader)loader).getURLs()).toString();
            }
            // throw new IOException("Native library (" + resourcePath + ") not found in resource path (" + path + ")");
            System.out.println("Native library (" + resourcePath + ") not found in resource path (" + path + ")");
        }
        if (DEBUG) {
            System.out.println("Found library resource at " + url);
        }

        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            try {
                lib = new File(new URI(url.toString()));
            }
            catch(final URISyntaxException e) {
                lib = new File(url.getPath());
            }
            if (DEBUG) {
                System.out.println("Looking in " + lib.getAbsolutePath());
            }
            if (!lib.exists()) {
                throw new IOException("File URL " + url + " could not be properly decoded");
            }
        }
        else if (!Boolean.getBoolean("jna.nounpack")) {
            final InputStream is = loader.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Can't obtain InputStream for " + resourcePath);
            }

            FileOutputStream fos = null;
            try {
                // Suffix is required on windows, or library fails to load
                // Let Java pick the suffix, except on windows, to avoid
                // problems with Web Start.
                final File dir = getTempDir();
                lib = File.createTempFile("jna", Platform.isWindows()?".dat":null, dir);
                if (!Boolean.getBoolean("jnidispatch.preserve")) {
                    lib.deleteOnExit();
                }
                fos = new FileOutputStream(lib);
                int count;
                final byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch(final IOException e) {
                throw new IOException("Failed to create temporary file for " + name + " library: " + e.getMessage());
            }
            finally {
                try { is.close(); } catch(final IOException e) { }
                if (fos != null) {
                    try { fos.close(); } catch(final IOException e) { }
                }
            }
        }
        return lib;
    }

    static File getTempDir() throws IOException {
        File jnatmp;
        final String prop = System.getProperty("jna.tmpdir");
        if (prop != null) {
            jnatmp = new File(prop);
            jnatmp.mkdirs();
        }
        else {
            final File tmp = new File(System.getProperty("java.io.tmpdir"));
            // Loading DLLs via System.load() under a directory with a unicode
            // name will fail on windows, so use a hash code of the user's
            // name in case the user's name contains non-ASCII characters
            jnatmp = new File(tmp, "jna-" + System.getProperty("user.name").hashCode());
            jnatmp.mkdirs();
            if (!jnatmp.exists() || !jnatmp.canWrite()) {
                jnatmp = tmp;
            }
        }
        if (!jnatmp.exists()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' does not exist");
        }
        if (!jnatmp.canWrite()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' is not writable");
        }
        return jnatmp;
    }

    /*  */
    private ImagePanel getImagePanel()
    {
        if (imgPanel == null) {
            imgPanel = new ImagePanel();
            imgPanel.setLayout(null);
            imgPanel.setBounds(new Rectangle(260, 17, 270, 310));

        }
        return imgPanel;
    }

    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */
    private JList getJList()
    {
        if (jList == null) {
            jList = new JList();
            jList.setBounds(new Rectangle(417, 19, 0, 0));
        }
        return jList;
    }

    /**
     * This method initializes jTextField_status
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField_status()
    {
        if (jTextField_status == null) {
            jTextField_status = new JTextField();
            jTextField_status.setBounds(new Rectangle(1, 554, 498, 16));
        }
        return jTextField_status;
    }

    /**
     * This method initializes jButton_update
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_update()
    {
        if (jButton_update == null) {
            jButton_update = new JButton();
            jButton_update.setBounds(new Rectangle(91, 15, 76, 21));
            jButton_update.setText("Update");

            jButton_update.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()

                    final int nRes = libScanner.UFS_Update();
                    if (nRes == 0) {
                    } else {
                    }
                }
            });
        }
        return jButton_update;
    }

    /**
     * This method initializes jButton_Uninit
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_Uninit()
    {
        if (jButton_Uninit == null) {
            jButton_Uninit = new JButton();
            jButton_Uninit.setBounds(new Rectangle(181, 15, 72, 21));
            jButton_Uninit.setText("Uninit");

            jButton_Uninit.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()
                    int nRes = libScanner.UFS_Uninit();
                    if (nRes == 0) {
                        setStatusMsg("UFS_Uninit sucess!!");
                        nRes = libMatcher.UFM_Delete(hMatcher);
                        nInitFlag = 0;
                        MsgBox("UFS_Uninit success!");
                    } else {
                        setStatusMsg("UFS_Uninit fail!!");
                    }
                }
            });
        }
        return jButton_Uninit;
    }

    /**
     * This method initializes jList1_scanner_list
     *
     * @return javax.swing.JList
     */
    private JList getJList1_scanner_list()
    {

        if (jList1_scanner_list == null) {
            listModel = new DefaultListModel();
            jList1_scanner_list = new JList(listModel);
            jList1_scanner_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jList1_scanner_list.setSelectedIndex(0);
            jList1_scanner_list.setVisibleRowCount(5);
            jList1_scanner_list.setBounds(new Rectangle(15, 66, 234, 103));

            listScrollPane = new JScrollPane(jList1_scanner_list);
            listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            jList1_scanner_list.setVisible(true);
            jList1_scanner_list.addListSelectionListener(new javax.swing.event.ListSelectionListener()
            {
                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent e)
                {
                    getCurrentScannerInfo();
                }
            });
        }
        return jList1_scanner_list;
    }

    /**
     * This method initializes jComboBox_timeout
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_timeout()
    {
        if (jComboBox_timeout == null) {
            jComboBox_timeout = new JComboBox();
            jComboBox_timeout.setBounds(new Rectangle(80, 195, 50, 23));
            jComboBox_timeout.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }
                    jComboBox_timeout = (JComboBox) e.getSource();
                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_timeout.getSelectedItem()));

                    // set parameter
                    final IntByReference pValue = new IntByReference();
                    pValue.setValue(nSelectedIdx * 1000);

                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_TIMEOUT, pValue); // 201
                                                                                                                // :
                                                                                                                // timeout
                                                                                                                // parameter
                        if (nRes == 0) {
                            setStatusMsg("Change combox-timeout,201(timeout) value is " + pValue.getValue());
                        } else {
                            setStatusMsg("Change combox-timeout,change parameter value fail! code: " + nRes);
                        }
                    } else {
                        setStatusMsg("getCurrentScannerHandle fail!! in ChangeComboBox-timeout");
                    }

                }
            });

        }
        return jComboBox_timeout;
    }

    private JComboBox getJComboBox_detect_fake()
    {
        if (jComboBox_detect_fake == null) {
            jComboBox_detect_fake = new JComboBox();
            jComboBox_detect_fake.setBounds(new Rectangle(210, 195, 46, 18));

            jComboBox_detect_fake.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }
                    jComboBox_detect_fake = (JComboBox) e.getSource();
                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_detect_fake.getSelectedItem()));

                    // set parameter
                    final IntByReference pValue = new IntByReference();
                    pValue.setValue(nSelectedIdx * 1000);

                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_DETECT_FAKE, pValue); // 312
                                                                                                                    // :
                                                                                                                    // detect_fake
                                                                                                                    // parameter
                        if (nRes == 0) {
                            setStatusMsg("Change combox-detect_fake,312(detect_fake) value is " + pValue.getValue());
                        } else {
                            setStatusMsg("Change combox-detect_fake,change parameter value fail! code: " + nRes);
                        }
                    } else {
                        setStatusMsg("getCurrentScannerHandle fail!! in ChangeComboBox-detect_fake");
                    }
                }
            });

        }
        return jComboBox_detect_fake;
    }

    /**
     * This method initializes jButton_start_capturing
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_start_capturing()
    {
        if (jButton_start_capturing == null) {
            jButton_start_capturing = new JButton();
            jButton_start_capturing.setBounds(new Rectangle(16, 343, 130, 20));
            jButton_start_capturing.setText("Start capturing");
            jButton_start_capturing.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 1) {
                        Pointer hScanner = null;
                        hScanner = GetCurrentScannerHandle();
                        testCallStartCapturing(hScanner);
                    } else {
                        MsgBox("initiate!");
                    }
                }
            });
        }
        return jButton_start_capturing;
    }

    /**
     * This method initializes jButton_abort
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_abort()
    {
        if (jButton_abort == null) {
            jButton_abort = new JButton();
            jButton_abort.setBounds(new Rectangle(151, 343, 89, 20));
            jButton_abort.setText("abort");

            jButton_abort.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }

                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final int nRes = libScanner.UFS_AbortCapturing(hScanner);
                        if (nRes == 0) {
                            setStatusMsg("UFS_UFS_AbortCapturing success!!");
                        } else {
                            setStatusMsg("UFS_UFS_AbortCapturing fail!! code:" + nRes);
                        }
                    } else {
                        setStatusMsg("UFS_UFS_AbortCapturing fail!! get current scanner handle fail!");
                    }
                }
            });
        }
        return jButton_abort;
    }

    /**
     * This method initializes jButton_extractor
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_extractor()
    {
        if (jButton_extractor == null) {
            jButton_extractor = new JButton();
            jButton_extractor.setBounds(new Rectangle(151, 369, 89, 20));
            jButton_extractor.setText("extract");
            jButton_extractor.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }
                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
                        final IntByReference refTemplateSize = new IntByReference();
                        final IntByReference refTemplateQuality = new IntByReference();
                        int nRes;
                        final int nSelectedIdx = jComboBox_ScanType.getSelectedIndex();
                        switch (nSelectedIdx) {
                            case 0:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2001); // 2001
                                                                                       // Suprema
                                                                                       // type
                                break;
                            case 1:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2002); // 2002
                                                                                       // iso
                                                                                       // type
                                break;
                            case 2:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2003); // 2003
                                                                                       // ansi
                                                                                       // type
                                break;
                        }

                        nRes = libScanner.UFS_ExtractEx(hScanner, MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize,
                                        refTemplateQuality);
                        if (nRes == 0) {
                            setStatusMsg("UFS_ExtractEx success!! size:" + refTemplateSize.getValue() + "quality:"
                                            + refTemplateQuality.getValue());
                        } else {
                            setStatusMsg("UFS_ExtractEx fail!! code:" + nRes);
                            final byte[] refErr = new byte[512];
                            nRes = libScanner.UFS_GetErrorString(nRes, refErr);
                            if (nRes == 0) {
                                setStatusMsg("==>UFS_GetErrorString err is " + Native.toString(refErr));
                            }
                        }
                    } else {
                        setStatusMsg("UFS_ExtractEx fail!! get current scanner handle fail!");
                    }
                }
            });

        }
        return jButton_extractor;
    }

    /**
     * This method initializes jComboBox_enroll
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_enroll()
    {
        if (jComboBox_enroll == null) {
            jComboBox_enroll = new JComboBox();
            jComboBox_enroll.setBounds(new Rectangle(129, 406, 46, 16));
            jComboBox_enroll.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }
                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_enroll.getSelectedItem()));
                    setStatusMsg("jComboBox_enroll selected :" + nSelectedIdx);
                }
            });

        }
        return jComboBox_enroll;
    }

    /**
     * This method initializes jList_msg_log
     *
     * @return javax.swing.JList
     */
    private JList getJList_msg_log()
    {
        if (jList_msg_log == null) {

            /* add */
            listLogModel = new DefaultListModel();
            jList_msg_log = new JList(listLogModel);
            jList_msg_log.setBounds(new Rectangle(13, 476, 406, 66));
            jList_msg_log.setAutoscrolls(true);

            listLogScrollPane = new JScrollPane(jList_msg_log);
            listLogScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            listLogScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            jList_msg_log.addListSelectionListener(new javax.swing.event.ListSelectionListener()
            {
                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent e)
                {
                    System.out.println("valueChanged()"); // TODO Auto-generated
                                                          // Event stub
                                                          // valueChanged()
                }
            });
        }
        return jList_msg_log;
    }

    /**
     * This method initializes jComboBox_security_level
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_security_level()
    {
        if (jComboBox_security_level == null) {
            jComboBox_security_level = new JComboBox();
            jComboBox_security_level.setBounds(new Rectangle(353, 345, 121, 18));
            jComboBox_security_level.addActionListener(new java.awt.event.ActionListener()
            {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        // return;
                    } else {
                        final int nSelectedIdx = jComboBox_security_level.getSelectedIndex();
                        if (nSelectedIdx != -1) {
                            // MsgBox("selected Idx:"+nSelectedIdx);

                            final IntByReference pValue = new IntByReference();
                            pValue.setValue(nSelectedIdx + 1);

                            final int nRes = libMatcher.UFM_SetParameter(hMatcher, libMatcher.UFM_PARAM_SECURITY_LEVEL,
                                            pValue); // 302
                            // :
                            // security
                            // level
                            // :UFM_
                            if (nRes == 0) {
                                setStatusMsg("Change combox-security level,302(security) value is "
                                                + pValue.getValue());
                            } else {
                                setStatusMsg("Change combox-security level,change parameter value fail! code: " + nRes);
                            }
                        }
                    }

                }
            });

        }
        return jComboBox_security_level;
    }

    /**
     * This method initializes jCheckBox_fastmode
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBox_fastmode()
    {
        if (jCheckBox_fastmode == null) {
            jCheckBox_fastmode = new JCheckBox();
            jCheckBox_fastmode.setBounds(new Rectangle(262, 384, 91, 24));
            jCheckBox_fastmode.setText("Fast mode");

            jCheckBox_fastmode.addActionListener(new java.awt.event.ActionListener()
            {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        // return;
                    } else {
                        final IntByReference pValue = new IntByReference();
                        Pointer hScanner = null;
                        hScanner = GetCurrentScannerHandle();

                        if (hScanner == null) {
                            setStatusMsg("getCurrentScannerHandle fail!! in checkbox-fastmode");
                            // return;
                        } else {
                            if (jCheckBox_fastmode.isSelected()) {
                                // set parameter
                                pValue.setValue(1);

                                final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_USE_SIF,
                                                pValue); // 301:fast mode
                                if (nRes == 0) {
                                    setStatusMsg("Change checkbox-fastmode core,301 value is " + pValue.getValue());
                                } else {
                                    setStatusMsg("Change checkbox-fastmode core,change parameter value fail! code: "
                                                    + nRes);
                                }

                            } else {
                                // set parameter
                                pValue.setValue(0);

                                final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_USE_SIF,
                                                pValue); // 301:fast mode
                                if (nRes == 0) {
                                    setStatusMsg("Change checkbox-fastmode core,301 value is " + pValue.getValue());
                                } else {
                                    setStatusMsg("Change checkbox-fastmode core,change parameter value fail! code: "
                                                    + nRes);
                                }
                            }
                        }
                    }
                }
            });

        }
        return jCheckBox_fastmode;
    }

    /**
     * This method initializes jComboBox_enrollid
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_enrollid()
    {
        if (jComboBox_enrollid == null) {
            jComboBox_enrollid = new JComboBox();
            jComboBox_enrollid.setBounds(new Rectangle(315, 414, 59, 23));
            jComboBox_enrollid.addActionListener(new java.awt.event.ActionListener()
            {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_enrollid.getSelectedItem()));

                }
            });

        }
        return jComboBox_enrollid;
    }

    /**
     * This method initializes jButton_verity
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_verity()
    {
        if (jButton_verity == null) {
            jButton_verity = new JButton();
            jButton_verity.setBounds(new Rectangle(393, 396, 83, 19));
            jButton_verity.setText("Verify");
            jButton_verity.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    final int nSelectedIdx = jComboBox_enrollid.getSelectedIndex();
                    if (nSelectedIdx == -1) {
                        MsgBox("selet enroll id");
                        //return;
                    } else {
                     // MsgBox(" enroll id:"+nSelectedIdx + " place a finger");

                        Pointer hScanner = null;
                        hScanner = GetCurrentScannerHandle();

                        if (hScanner != null) {
                            libScanner.UFS_ClearCaptureImageBuffer(hScanner);

                            setStatusMsg("Place a finger");

                            int nRes = testCallStartCapturing(hScanner);
                            if (nRes != 0) {
                                setStatusMsg("caputure single image fail!! " + nRes);
                                return;
                            }

                            final byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
                            final PointerByReference refError;
                            final IntByReference refTemplateSize = new IntByReference();
                            final IntByReference refTemplateQuality = new IntByReference();
                            final IntByReference refVerify = new IntByReference();

                            try {
                                Thread.sleep(500);
                                nRes = libScanner.UFS_ExtractEx(hScanner, MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize,
                                                refTemplateQuality);
                                if (nRes == 0) {
                                    nRes = libMatcher.UFM_Verify(hMatcher, bTemplate, refTemplateSize.getValue(),
                                                    byteTemplateArray[nSelectedIdx], intTemplateSizeArray[nSelectedIdx],
                                                    refVerify);// byte[][]
                                    if (nRes == 0) {
                                        if (refVerify.getValue() == 1) {
                                            Thread.sleep(5000);
                                            setStatusMsg("verify success!! enroll_id: " + (nSelectedIdx + 1));
                                            System.out.println("verify success!! enroll_id: " + (nSelectedIdx + 1));
                                            MsgBox("verify success!! enroll_id: " + (nSelectedIdx + 1));
                                        } else {
                                            Thread.sleep(5000);
                                            setStatusMsg("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                                            System.out.println("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                                            MsgBox("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                                        }
                                    } else {
                                        setStatusMsg("verify fail!! " + nRes);
                                        final byte[] refErr = new byte[512];
                                        nRes = libMatcher.UFM_GetErrorString(nRes, refErr);
                                        if (nRes == 0) {
                                            setStatusMsg("==>UFM_GetErrorString err is " + Native.toString(refErr));
                                            MsgBox("==>UFM_GetErrorString err is " + Native.toString(refErr));
                                        }
                                    }

                                } else {
                                    setStatusMsg("extract template fail!! " + nRes);
                                }
                            } catch (final InterruptedException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } else {
                            setStatusMsg("getCurrentScannerHandle fail!! ");
                            //return;
                        }
                    }
                }
            });

        }
        return jButton_verity;
    }

    /**
     * This method initializes jButton_Identify
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_Identify()
    {
        if (jButton_Identify == null) {
            jButton_Identify = new JButton();
            jButton_Identify.setBounds(new Rectangle(393, 421, 83, 20));
            jButton_Identify.setText("Identify");
            jButton_Identify.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();

                    if (hScanner != null) {
                        libScanner.UFS_ClearCaptureImageBuffer(hScanner);
                        setStatusMsg("Place a finger");

                        int nRes = testCallStartCapturing(hScanner);
                        if (nRes != 0) {
                            setStatusMsg("caputure single image fail!! " + nRes);
                            return;
                        }

                        final byte[] bTemplate = new byte[1024];

                        final IntByReference refTemplateSize = new IntByReference();
                        final IntByReference refTemplateQuality = new IntByReference();

                        try {
                            Thread.sleep(500);
                            nRes = libScanner.UFS_ExtractEx(hScanner, MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize,
                                            refTemplateQuality);
                            if (nRes == 0) {
                                libMatcher.UFM_IdentifyInit(hMatcher, bTemplate, refTemplateSize.getValue());

                                int nMatchResult = 0;
                                final IntByReference refIdentifyRes = new IntByReference();
                                int i = 0;

                                for (i = 0; i < nTemplateCnt; i++) {
                                    nRes = libMatcher.UFM_IdentifyNext(hMatcher, byteTemplateArray[i],
                                                    intTemplateSizeArray[i], refIdentifyRes);
                                    if (nRes == 0) {
                                        if (refIdentifyRes.getValue() == 1) {
                                            setStatusMsg("Identfy success!!  match index number:" + (i + 1));
                                            MsgBox("Identfy success!! index number:" + (i + 1));
                                            nMatchResult = 1;
                                            break;
                                        } else {

                                        }
                                    }
                                }

                                if (nMatchResult != 1) {
                                    MsgBox("Identfy fail!!");
                                }
                            } else {
                                setStatusMsg("extract template fail!! " + nRes);
                                MsgBox("Identfy fail!!");
                                // return;
                            }
                        } catch (final InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        setStatusMsg("getCurrentScannerHandle fail!! ");
                        // return;
                    }
                }
            });

        }
        return jButton_Identify;
    }

    /**
     * This method initializes jButton_enroll
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_enroll()
    {
        if (jButton_enroll == null) {
            jButton_enroll = new JButton();
            jButton_enroll.setBounds(new Rectangle(147, 442, 92, 20));
            jButton_enroll.setText("Enroll");
            jButton_enroll.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        // return;
                    } else {
                        Pointer hScanner = null;
                        hScanner = GetCurrentScannerHandle();
                        if (hScanner != null) {
                            int nRes = libScanner.UFS_ClearCaptureImageBuffer(hScanner);
                            setStatusMsg("place a finger");
                            nRes = testCallStartCapturing(hScanner);
                            setStatusMsg("capture single image");
                            if (nRes == 0) {
                                final byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
                                final IntByReference refTemplateSize = new IntByReference();
                                final IntByReference refTemplateQuality = new IntByReference();
                                try {
                                    // Es necesario un sleep para que termine el scanneo antes de comenzar a extraer.
                                    Thread.sleep(1000);
                                    nRes = libScanner.UFS_ExtractEx(hScanner, MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize,
                                                    refTemplateQuality);
                                    if (nRes == 0) {
                                        setStatusMsg("save template file template size:" + refTemplateSize.getValue()
                                                        + " quality:" + refTemplateQuality.getValue());

                                        final int nSelectedValue = Integer
                                                        .parseInt((String) (jComboBox_enroll.getSelectedItem()));
                                        System.out.println("template quality = " + refTemplateQuality.getValue());
                                        if (refTemplateQuality.getValue() < nSelectedValue) {
                                            MsgBox("template quality < " + nSelectedValue);
                                            // return;
                                        } else {
                                            if (nTemplateCnt > 99) {
                                                setStatusMsg("template queue full!! limited 100 template, now "
                                                                + nTemplateCnt);
                                                MsgBox("template queue full!! limited 100 template, now "
                                                                + nTemplateCnt);
                                                // return;
                                            } else {
                                                final int tempsize = refTemplateSize.getValue();

                                                System.arraycopy(bTemplate, 0, byteTemplateArray[nTemplateCnt], 0,
                                                                refTemplateSize.getValue());// byte[][]

                                                intTemplateSizeArray[nTemplateCnt] = refTemplateSize.getValue();

                                                setStatusMsg("eroll template array idx:" + nTemplateCnt
                                                                + " template size:"
                                                                + intTemplateSizeArray[nTemplateCnt]);
                                                nTemplateCnt++;

                                                drawCurrentFingerImage();

                                                String szComboData = null;
                                                szComboData = String.valueOf(nTemplateCnt);

                                                jComboBox_enrollid.insertItemAt(szComboData, nTemplateCnt - 1);
                                                jComboBox_enrollid.setSelectedIndex(nTemplateCnt - 1);
                                                nCaptureFlag = 1;
                                            }
                                        }
                                    } else {
                                        setStatusMsg("Enroll Image fail!! code:" + nRes);
                                        final byte[] refErr = new byte[512];
                                        nRes = libScanner.UFS_GetErrorString(nRes, refErr);
                                        if (nRes == 0) {
                                            System.out.println("==>UFS_GetErrorString err is "
                                                            + Native.toString(refErr));
                                        }
                                    }
                                } catch (final Exception ex) {
                                    // MsgBox("exception err:"+ex.getMessage());
                                }
                            }
                        } else {
                            // scanner pointer null
                        }
                    }
                }
            });

        }
        return jButton_enroll;
    }

    /**
     * This method initializes jButton_save_tmp
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_save_tmp()
    {
        if (jButton_save_tmp == null) {
            jButton_save_tmp = new JButton();
            jButton_save_tmp.setBounds(new Rectangle(262, 443, 114, 21));
            jButton_save_tmp.setText("save template");
            jButton_save_tmp.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        // return;
                    } else {
                        final int nSelectedIdx = jComboBox_enrollid.getSelectedIndex();
                        if (nSelectedIdx == -1) {
                            MsgBox("enroll finger");
                            // return;
                        } else {
                         // MsgBox("idx:"+nSelectedIdx);

                            File file = null;
                            String szPath = null;

                            final JFileChooser fc = new JFileChooser();

                            final int returnVal = fc.showSaveDialog(AppletDemo.this);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                file = fc.getSelectedFile();
                                // This is where a real application would open the file.
                                setStatusMsg("saved: " + file.getName());

                                szPath = file.getAbsolutePath();

                                // MsgBox(szPath);
                                try {
                                    final byte[] filebA = new byte[intTemplateSizeArray[nSelectedIdx]];
                                    // array copy: (src,offset,target,offset,copy size)
                                    System.arraycopy(byteTemplateArray[nSelectedIdx], 0, filebA, 0,
                                                    intTemplateSizeArray[nSelectedIdx]);// byte[][]
                                    // byteTemplateArray.getPointer().read(0,filebA,nTemplateCnt*MAX_TEMPLATE_SIZE,
                                        //intTemplateSizeArray[nSelectedIdx]);

                                    final RandomAccessFile rf = new RandomAccessFile(szPath, "rw");
                                    rf.write(filebA);
                                    rf.close();

                                    setStatusMsg("write template success,filename is " + szPath);
                                    MsgBox("write template success,filename is " + szPath);

                                } catch (final Exception ex) {
                                    MsgBox("save template file fail!! :" + ex.getMessage());
                                    setStatusMsg("write template fail err : " + ex.getMessage());
                                }
                            } else {
                                setStatusMsg("saved command cancelled by user");
                                // return;
                            }
                        }
                    }
                }
            });

        }
        return jButton_save_tmp;
    }

    /**
     * This method initializes jButton_save_img
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_save_img()
    {
        if (jButton_save_img == null) {
            jButton_save_img = new JButton();
            jButton_save_img.setBounds(new Rectangle(380, 443, 106, 21));
            jButton_save_img.setText("save image");
            jButton_save_img.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        // return;
                    } else {
                        if (nCaptureFlag != 0) {
                            Pointer hScanner = null;
                            String szPath = null;

                            hScanner = GetCurrentScannerHandle();
                            if (hScanner != null) {
                                File file = null;
                                final JFileChooser fc = new JFileChooser();
                                final int returnVal = fc.showSaveDialog(AppletDemo.this);
                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    file = fc.getSelectedFile();
                                    // This is where a real application would open the
                                    // file.
                                    setStatusMsg("saved: " + file.getName());
                                } else {
                                    setStatusMsg("saved command cancelled by user");
                                    return;
                                }

                                szPath = file.getAbsolutePath();
                                MsgBox(szPath);

                                final int nRes = libScanner.UFS_SaveCaptureImageBufferToBMP(hScanner, szPath);
                                if (nRes == 0) {
                                    MsgBox("Captured image is saved\r\n");
                                } else {
                                }
                            } else {
                            }
                        } else {
                            // return;
                        }
                    }


                }
            });

        }
        return jButton_save_img;
    }

    /**
     * This method initializes jButton_clear
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton_clear()
    {
        if (jButton_clear == null) {
            jButton_clear = new JButton();
            jButton_clear.setBounds(new Rectangle(422, 481, 66, 58));
            jButton_clear.setText("clear");
            jButton_clear.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    listLogModel.clear();
                    nLogListCnt = 0;

                    /* test draw image */
                    final IntByReference refResolution = new IntByReference();
                    final IntByReference refHeight = new IntByReference();
                    final IntByReference refWidth = new IntByReference();
                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();

                    int nRes = libScanner.UFS_GetCaptureImageBufferInfo(hScanner, refWidth, refHeight, refResolution);
                    final byte[] pImageData = new byte[refWidth.getValue() * refHeight.getValue()];
                    nRes = libScanner.UFS_GetCaptureImageBuffer(hScanner, pImageData);

                    /******** image draw test *********/
                    imgPanel.drawFingerImage(refWidth.getValue(), refHeight.getValue(), pImageData);
                }
            });
        }
        return jButton_clear;
    }

    /**
     * This method initializes jComboBox_bri
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_bri()
    {
        if (jComboBox_bri == null) {
            jComboBox_bri = new JComboBox();
            jComboBox_bri.setBounds(new Rectangle(80, 221, 50, 23));
            jComboBox_bri.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }
                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_bri.getSelectedItem()));

                    // MsgBox("jComboBox_bri selected idx:"+nSelectedIdx);

                    // set parameter

                    final IntByReference pValue = new IntByReference();
                    pValue.setValue(nSelectedIdx);

                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_BRIGHTNESS, pValue); // 202
                                                                                                                   // :
                                                                                                                   // birghtness
                                                                                                                   // parameter
                        if (nRes == 0) {
                            setStatusMsg("Change combox-brightness,202(brightness) value is " + pValue.getValue());
                        } else {
                            setStatusMsg("Change combox-brightness,change parameter value fail! code: " + nRes);
                        }
                    } else {
                        setStatusMsg("getCurrentScannerHandle fail!! in ChangeComboBox-brightness");
                    }
                }
            });

        }
        return jComboBox_bri;
    }

    /**
     * This method initializes jComboBox_sens
     *
     * @return javax.swing.JComboBox
     */

    private JComboBox getJComboBox_sens()
    {
        if (jComboBox_sens == null) {
            jComboBox_sens = new JComboBox();
            jComboBox_sens.setBounds(new Rectangle(80, 248, 50, 23));
            jComboBox_sens.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    if (nInitFlag == 0) {
                        MsgBox("initiate!");
                        return;
                    }

                    final int nSelectedIdx = Integer.parseInt((String) (jComboBox_sens.getSelectedItem()));
                    // MsgBox("jComboBox_sens selected idx:"+nSelectedIdx);

                    // set parameter

                    final IntByReference pValue = new IntByReference();
                    pValue.setValue(nSelectedIdx);

                    Pointer hScanner = null;
                    hScanner = GetCurrentScannerHandle();
                    if (hScanner != null) {
                        final int nRes = libScanner.UFS_SetParameter(hScanner, libScanner.UFS_PARAM_SENSITIVITY, pValue); // 203
                                                                                                                    // :
                                                                                                                    // sensitivity
                                                                                                                    // parameter
                        if (nRes == 0) {
                            setStatusMsg("Change combox-sensitivity,203(sensitivity) value is " + pValue.getValue());
                        } else {
                            setStatusMsg("Change combox-sensitivity,change parameter value fail! code: " + nRes);
                        }
                    } else {
                        setStatusMsg("getCurrentScannerHandle fail!! in ChangeComboBox-sensitivity");
                    }
                }
            });

        }
        return jComboBox_sens;
    }

    /**
     * This method initializes jFingerInfo
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJFingerInfo()
    {
        if (jFingerInfo == null) {
            jFingerInfo = new JTextField();
            jFingerInfo.setBounds(new Rectangle(262, 283, 202, 18));
            jFingerInfo.setBounds(new Rectangle(267, 279, 202, 18));
        }
        return jFingerInfo;
    }

    /**
     * This method initializes jComboBox_ScanType
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_ScanType()
    {
        if (jComboBox_ScanType == null) {
            jComboBox_ScanType = new JComboBox();
            jComboBox_ScanType.setBounds(new Rectangle(108, 285, 112, 20));
            jComboBox_ScanType.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()
                    final int nSelectedIdx = jComboBox_ScanType.getSelectedIndex();
                    int nRes;
                    Pointer hScanner;
                    hScanner = GetCurrentScannerHandle();

                    if (hScanner != null) {
                        switch (nSelectedIdx) {
                            case 0:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2001); // 2001
                                                                                       // Suprema
                                                                                       // type
                                break;
                            case 1:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2002); // 2002
                                                                                       // iso
                                                                                       // type
                                break;
                            case 2:
                                nRes = libScanner.UFS_SetTemplateType(hScanner, 2003); // 2003
                                                                                       // ansi
                                                                                       // type
                                break;
                        }
                    }
                }
            });
        }
        return jComboBox_ScanType;
    }

    /**
     * This method initializes jComboBox_MatchType
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getJComboBox_MatchType()
    {
        if (jComboBox_MatchType == null) {
            jComboBox_MatchType = new JComboBox();
            jComboBox_MatchType.setBounds(new Rectangle(354, 369, 119, 19));
            jComboBox_MatchType.addActionListener(new java.awt.event.ActionListener()
            {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e)
                {
                    System.out.println("actionPerformed()"); // TODO
                                                             // Auto-generated
                                                             // Event stub
                                                             // actionPerformed()
                    final int nSelectedIdx = jComboBox_MatchType.getSelectedIndex();
                    int nRes;

                    if (hMatcher != null) {
                        switch (nSelectedIdx) {
                            case 0:
                                nRes = libMatcher.UFM_SetTemplateType(hMatcher, 2001); // 2001
                                                                                       // Suprema
                                                                                       // type
                                break;
                            case 1:
                                nRes = libMatcher.UFM_SetTemplateType(hMatcher, 2002); // 2002
                                                                                       // iso
                                                                                       // type
                                break;
                            case 2:
                                nRes = libMatcher.UFM_SetTemplateType(hMatcher, 2003); // 2003
                                                                                       // ansi
                                                                                       // type
                                break;
                        }
                    }
                }
            });
        }
        return jComboBox_MatchType;
    }

    /**
     * @param args
     */
    /*public static void main(final String[] args)
    {
        // TODO Auto-generated method stub

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                final AppletDemo thisClass = new AppletDemo();
                thisClass.setVisible(true);
            }
        });
    }*/

    /**
     * This is the default constructor
     */
    public AppletDemo()
    {
        super();
        setBackground(new Color(53, 66, 90));
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize()
    {
        this.setSize(545, 621);
        //this.add(getJContentPane());
        //this.setTitle("Suprema PC SDK Java AppletDemo (JAVA SWING)");
        getJContentPane();
    }

    @Override
    public void init() {
        System.setProperty("jna.library.path", "C:/autentia/bin/x64");
        initialize();
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

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private void getJContentPane()
    {
        //if (jContentPane == null) {
            jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(258, 373, 91, 13));
            jLabel1.setText("TemplateType");
            jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(16, 286, 89, 21));
            jLabel.setText("TemplateType");
            jLabel_scanner_list = new JLabel();
            jLabel_scanner_list.setBounds(new Rectangle(15, 44, 72, 18));
            jLabel_scanner_list.setText("Scanner List");
            jLabel_enrollid = new JLabel();
            jLabel_enrollid.setBounds(new Rectangle(264, 416, 46, 18));
            jLabel_enrollid.setText("Enroll ID");
            jLabel_security_levle = new JLabel();
            jLabel_security_levle.setBounds(new Rectangle(262, 345, 80, 18));
            jLabel_security_levle.setText("Security Level");
            jLabel_match = new JLabel();
            jLabel_match.setBounds(new Rectangle(263, 327, 35, 18));
            jLabel_match.setText("Match");
            jLabel_sense1 = new JLabel();
            jLabel_sense1.setBounds(new Rectangle(17, 325, 106, 16));
            jLabel_sense1.setText("Enroll");
            jLabel_parameter = new JLabel();
            jLabel_parameter.setBounds(new Rectangle(13, 173, 111, 18));
            jLabel_parameter.setText("Scanner parameter");
            jLabel_enroll = new JLabel();
            jLabel_enroll.setBounds(new Rectangle(16, 406, 106, 16));
            jLabel_enroll.setText("Enroll Quallity (%s)");
            jLabel_sense = new JLabel();
            jLabel_sense.setBounds(new Rectangle(14, 248, 62, 18));
            jLabel_sense.setText("Sensitivity");
            jLabel_brightness = new JLabel();
            jLabel_brightness.setBounds(new Rectangle(13, 220, 62, 18));
            jLabel_brightness.setText("Birghtness");
            jLabel_timeout = new JLabel();
            jLabel_timeout.setBounds(new Rectangle(13, 197, 46, 18));
            jLabel_timeout.setText("Timeout");
            jLabel_detect_fake = new JLabel();
            jLabel_detect_fake.setBounds(new Rectangle(135, 197, 70, 18));
            jLabel_detect_fake.setText("Fake Detect");
            jContentPane = new JPanel();

            this.setLayout(null);
            this.add(getJButton_ufe_init(), null);
            this.add(getJList(), null);
            this.add(getJTextField_status(), null);
            this.add(getJButton_update(), null);
            this.add(getJButton_Uninit(), null);
            this.add(getJComboBox_timeout(), null);
            this.add(jLabel_timeout, null);
            this.add(getJComboBox_detect_fake(), null);
            this.add(jLabel_detect_fake, null);
            this.add(jLabel_brightness, null);
            this.add(jLabel_sense, null);
            this.add(getJButton_start_capturing(), null);
            this.add(getJButton_abort(), null);
            this.add(getJButton_extractor(), null);
            this.add(jLabel_enroll, null);
            this.add(getJComboBox_enroll(), null);
            this.add(jLabel_parameter, null);
            this.add(getJList_msg_log());
            this.add(listLogScrollPane, null);
            this.add(jLabel_sense1, null);
            this.add(jLabel_match, null);
            this.add(jLabel_security_levle, null);
            this.add(getJComboBox_security_level(), null);
            this.add(getJCheckBox_fastmode(), null);
            this.add(jLabel_enrollid, null);
            this.add(getJComboBox_enrollid(), null);
            this.add(getJButton_verity(), null);
            this.add(getJButton_Identify(), null);
            this.add(getJButton_enroll(), null);
            this.add(getJButton_save_tmp(), null);
            this.add(getJButton_save_img(), null);
            this.add(jLabel_scanner_list, null);
            this.add(getJButton_clear(), null);
            this.add(getImagePanel(), null);
            this.add(getJList1_scanner_list());
            this.add(listScrollPane, null);
            this.add(getJComboBox_bri(), null);
            this.add(getJComboBox_sens(), null);
            this.add(getJFingerInfo(), null);
            this.add(getJComboBox_ScanType(), null);
            this.add(getJComboBox_MatchType(), null);
            this.add(jLabel, null);
            this.add(jLabel1, null);

        //return this;
    }
}
