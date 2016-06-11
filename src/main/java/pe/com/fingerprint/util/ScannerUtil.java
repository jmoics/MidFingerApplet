package pe.com.fingerprint.util;

import javax.swing.JOptionPane;

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
public class ScannerUtil
{
    public static int testCallScanProcCallback(final UFScanner libScanner,
                                               final UFScanner.UFS_SCANNER_PROC pScanProc)
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
     * Obtiene el manejador del scanner utilizando el indice que lo identifica en memoria (detectado en UpdateScannerList)
     * @return
     */
    public static Pointer getCurrentScannerHandle(final UFScanner _libScanner)
    {
        Pointer hScanner = null;
        int nRes = 0;
        int nNumber = 0;

        final PointerByReference refScanner = new PointerByReference();
        final IntByReference refScannerNumber = new IntByReference();

        // success!!//
        nRes = _libScanner.UFS_GetScannerNumber(refScannerNumber);

        if (nRes == 0) {
            nNumber = refScannerNumber.getValue();
            if (nNumber > 0) {
                nRes = _libScanner.UFS_GetScannerHandle(0, refScanner);
                hScanner = refScanner.getValue();
            }
        }
        return hScanner;
    }

    public static void initVariable(final UFScanner _libScanner,
                                    final UFMatcher _libMatcher,
                                    final Pointer _hMatcher)
    {
        Pointer hScanner = null;
        hScanner = getCurrentScannerHandle(_libScanner);

        final IntByReference pValue = new IntByReference();

        pValue.setValue(5000);

        int nRes = _libScanner.UFS_SetParameter(hScanner, UFScanner.UFS_PARAM_TIMEOUT, pValue);
        if (nRes == 0) {
            System.out.println("Setting timeout,201(timeout) value is " + pValue.getValue());
        } else {
            System.out.println("Setting timeout,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(100);
        nRes = _libScanner.UFS_SetParameter(hScanner, UFScanner.UFS_PARAM_BRIGHTNESS, pValue);
        if (nRes == 0) {
            System.out.println("Setting brightness,202 value is " + pValue.getValue());
        } else {
            System.out.println("Setting brightness,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(2);
        nRes = _libScanner.UFS_SetParameter(hScanner, UFScanner.UFS_PARAM_DETECT_FAKE, pValue);
        if (nRes == 0) {
            System.out.println("Setting detect_fake,312(fake detect) value is " + pValue.getValue());
        } else {
            System.out.println("Setting detect_fake,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(4);
        nRes = _libScanner.UFS_SetParameter(hScanner, UFScanner.UFS_PARAM_SENSITIVITY, pValue);
        if (nRes == 0) {
            System.out.println("Setting Sensitivity,203 value is " + pValue.getValue());
        } else {
            System.out.println("Setting Sensitivity, change parameter value fail! code: " + nRes);
        }

        nRes = _libScanner.UFS_SetTemplateType(hScanner, UFScanner.UFS_TEMPLATE_TYPE_SUPREMA); // 2001 Suprema type
        if (nRes == 0) {
            System.out.println("Setting TemplateType:2001");
        } else {
            System.out.println("Setting TemplateType, change parameter value fail! code: " + nRes);
        }

        pValue.setValue(0);
        nRes = _libScanner.UFS_SetParameter(hScanner, UFScanner.UFS_PARAM_USE_SIF, pValue); // 301:fast mode
        if (nRes == 0) {
            System.out.println("Setting fastmode core,301 value is " + pValue.getValue());
        } else {
            System.out.println("Setting fastmode core,change parameter value fail! code: " + nRes);
        }

        pValue.setValue(5);
        nRes = _libMatcher.UFM_SetParameter(_hMatcher, UFMatcher.UFM_PARAM_SECURITY_LEVEL, pValue); // 302
        if (nRes == 0) {
            System.out.println("Setting Security Level, value is " + pValue.getValue());
        } else {
            System.out.println("Setting Security Level, change parameter value fail! code: " + nRes);
        }

        nRes = _libMatcher.UFM_SetTemplateType(_hMatcher, UFMatcher.UFM_TEMPLATE_TYPE_SUPREMA); // 2001 Suprema type
        if (nRes == 0) {
            System.out.println("Setting TemplateType: 2001");
        } else {
            System.out.println("Setting TemplateType, change parameter value fail! code: " + nRes);
        }
    }

    /**
     * Especie de listener que detecta cuando se conecta o se desconecta el dispositivo scanner (luego del Init).
     */
    public static Pointer updateScannerList(final UFScanner _libScanner)
    {
        int nRes = 0;
        final PointerByReference hTempScanner = new PointerByReference();
        Pointer hScanner = null;
        final IntByReference refType = new IntByReference();

        final byte[] bScannerId = new byte[512];

        String szListLog = null;
        int nNumber = 0;

        System.out.println("==update Scanner list==");

        final IntByReference refNumber = new IntByReference();
        nRes = _libScanner.UFS_GetScannerNumber(refNumber);

        if (nRes == 0) {
            System.out.println("UFS_GetScannerNumber() res value is " + nRes);
            nNumber = refNumber.getValue();

            if (nNumber > 0) {
                for (int j = 0; j < nNumber; j++) {
                    nRes = _libScanner.UFS_GetScannerHandle(j, hTempScanner);
                    hScanner = null;
                    hScanner = hTempScanner.getValue();

                    if (nRes == 0 && hScanner != null) {
                        nRes = _libScanner.UFS_GetScannerID(hScanner, bScannerId);
                        nRes = _libScanner.UFS_GetScannerType(hScanner, refType);
                        szListLog = "ID : " + Native.toString(bScannerId) + " Type : " + refType.getValue();
                        System.out.println("Scanner: " + szListLog);
                    }
                }
            } else {
                System.out.println("Not Scanner loaded");
            }
        }
        return hScanner;
    }

    /**
     * Inicia el listener para captura de la imagen.
     * @return
     */
    public static int callStartCapturing(final UFScanner _libScanner,
                                         final Pointer _hScanner,
                                         final UFScanner.UFS_CAPTURE_PROC _pCaptureProc)
    {
        int nRes = 0;
        final PointerByReference refParam = new PointerByReference();

        if (_hScanner != null) {
            System.out.println("UFS_StartCapturing,get current scanner handle success! : " + _hScanner);

            // inicia el listener de captura de huella para que pueda inicializarse y capturar la imagen
            nRes = _libScanner.UFS_StartCapturing(_hScanner, _pCaptureProc, refParam);

            if (nRes == 0) {
                System.out.println("UFS_StartCapturing success!!");
                System.out.println("UFS_StartCapturing success!!");
                //nCaptureFlag = 1;
            } else {
                System.out.println("UFS_StartCapturing fail!! code:" + nRes);
            }
        } else {
            System.out.println("UFS_StartCapturing,GetScannerHandle fail!!");
            System.out.println("UFS_StartCapturing,get Scanner handle fail!!");
        }
        return nRes;
    }

    public static void MsgBox(final String log)
    {
        JOptionPane.showMessageDialog(null, log);
    }

    public static void showErrorString(final UFScanner _libScanner,
                                 Integer _nRes)
    {
        System.out.println("Enroll Image fail!! code:" + _nRes);
        final byte[] refErr = new byte[512];
        _nRes = _libScanner.UFS_GetErrorString(_nRes, refErr);
        if (_nRes == 0) {
            System.out.println("==>UFS_GetErrorString err is " + Native.toString(refErr));
        }
    }
}
