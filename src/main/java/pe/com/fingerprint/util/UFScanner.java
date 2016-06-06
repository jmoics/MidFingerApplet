package pe.com.fingerprint.util;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCall;
import com.sun.jna.win32.StdCallLibrary;

public abstract interface UFScanner
    extends StdCallLibrary
{

    public static final int UFS_OK = 0;
    public static final int UFS_STATUS = 0;
    public static final int UFS_ERROR = -1;
    public static final int UFS_ERR_NO_LICENSE = -101;
    public static final int UFS_ERR_LICENSE_NOT_MATCH = -102;
    public static final int UFS_ERR_LICENSE_EXPIRED = -103;
    public static final int UFS_ERR_NOT_SUPPORTED = -111;
    public static final int UFS_ERR_INVALID_PARAMETERS = -112;
    public static final int UFS_ERR_ALREADY_INITIALIZED = -201;
    public static final int UFS_ERR_NOT_INITIALIZED = -202;
    public static final int UFS_ERR_DEVICE_NUMBER_EXCEED = -203;
    public static final int UFS_ERR_LOAD_SCANNER_LIBRARY = -204;
    public static final int UFS_ERR_CAPTURE_RUNNING = -211;
    public static final int UFS_ERR_CAPTURE_FAILED = -212;
    public static final int UFS_ERR_FAKE_FINGER = -221;
    public static final int UFS_ERR_FINGER_ON_SENSOR = -231;
    public static final int UFS_ERR_NOT_GOOD_IMAGE = -301;
    public static final int UFS_ERR_EXTRACTION_FAILED = -302;
    public static final int UFS_ERR_CORE_NOT_DETECTED = -351;
    public static final int UFS_ERR_CORE_TO_LEFT = -352;
    public static final int UFS_ERR_CORE_TO_LEFT_TOP = -353;
    public static final int UFS_ERR_CORE_TO_TOP = -354;
    public static final int UFS_ERR_CORE_TO_RIGHT_TOP = -355;
    public static final int UFS_ERR_CORE_TO_RIGHT = -356;
    public static final int UFS_ERR_CORE_TO_RIGHT_BOTTOM = -357;
    public static final int UFS_ERR_CORE_TO_BOTTOM = -358;
    public static final int UFS_ERR_CORE_TO_LEFT_BOTTOM = -359;
    public static final int UFS_PARAM_TIMEOUT = 201;
    public static final int UFS_PARAM_BRIGHTNESS = 202;
    public static final int UFS_PARAM_SENSITIVITY = 203;
    public static final int UFS_PARAM_SERIAL = 204;
    public static final int UFS_PARAM_DETECT_CORE = 301;
    public static final int UFS_PARAM_TEMPLATE_SIZE = 302;
    public static final int UFS_PARAM_USE_SIF = 311;
    public static final int UFS_PARAM_CHECK_ENROLL_QUALITY = 321;
    public static final int UFS_PARAM_DETECT_FAKE = 312;
    public static final int UFS_SCANNER_TYPE_SFR200 = 1001;
    public static final int UFS_SCANNER_TYPE_SFR300 = 1002;
    public static final int UFS_SCANNER_TYPE_SFR300v2 = 1003;
    public static final int UFS_SCANNER_TYPE_SFR500 = 1004;
    public static final int UFS_SCANNER_TYPE_SFR600 = 1005;
    public static final int UFS_TEMPLATE_TYPE_SUPREMA = 2001;
    public static final int UFS_TEMPLATE_TYPE_ISO19794_2 = 2002;
    public static final int UFS_TEMPLATE_TYPE_ANSI378 = 2003;

    public abstract int UFS_Init();

    public abstract int UFS_Update();

    public abstract int UFS_Uninit();

    public abstract int UFS_SetScannerCallback(UFS_SCANNER_PROC paramUFS_SCANNER_PROC,
                                               PointerByReference paramPointerByReference);

    public abstract int UFS_RemoveScannerCallback();

    public abstract int UFS_GetScannerNumber(IntByReference paramIntByReference);

    public abstract int UFS_GetScannerHandle(int paramInt,
                                             PointerByReference paramPointerByReference);

    public abstract int UFS_GetScannerHandleByID(String paramString,
                                                 PointerByReference paramPointerByReference);

    public abstract int UFS_GetScannerIndex(Pointer paramPointer,
                                            IntByReference paramIntByReference);

    public abstract int UFS_GetScannerID(Pointer paramPointer,
                                         byte[] paramArrayOfByte);

    public abstract int UFS_GetCompanyID(Pointer paramPointer,
                                         byte[] paramArrayOfByte);

    public abstract int UFS_GetScannerType(Pointer paramPointer,
                                           IntByReference paramIntByReference);

    public abstract int UFS_GetParameter(Pointer paramPointer,
                                         int paramInt,
                                         IntByReference paramIntByReference);

    public abstract int UFS_SetParameter(Pointer paramPointer,
                                         int paramInt,
                                         IntByReference paramIntByReference);

    public abstract int UFS_IsSensorOn(Pointer paramPointer,
                                       IntByReference paramIntByReference);

    public abstract int UFS_IsFingerOn(Pointer paramPointer,
                                       IntByReference paramIntByReference);

    public abstract int UFS_CaptureSingleImage(Pointer paramPointer);

    public abstract int UFS_StartCapturing(Pointer paramPointer,
                                           UFS_CAPTURE_PROC paramUFS_CAPTURE_PROC,
                                           PointerByReference paramPointerByReference);

    public abstract int UFS_StartAutoCapture(Pointer paramPointer,
                                             UFS_CAPTURE_PROC paramUFS_CAPTURE_PROC,
                                             PointerByReference paramPointerByReference);

    public abstract int UFS_IsCapturing(Pointer paramPointer,
                                        IntByReference paramIntByReference);

    public abstract int UFS_AbortCapturing(Pointer paramPointer);

    public abstract int UFS_Extract(Pointer paramPointer,
                                    byte[] paramArrayOfByte,
                                    IntByReference paramIntByReference1,
                                    IntByReference paramIntByReference2);

    public abstract int UFS_ExtractEx(Pointer paramPointer,
                                      int paramInt,
                                      byte[] paramArrayOfByte,
                                      IntByReference paramIntByReference1,
                                      IntByReference paramIntByReference2);

    public abstract int UFS_SetEncryptionKey(Pointer paramPointer,
                                             String paramString);

    public abstract int UFS_EncryptTemplate(Pointer paramPointer,
                                            byte[] paramArrayOfByte1,
                                            int paramInt,
                                            byte[] paramArrayOfByte2,
                                            IntByReference paramIntByReference);

    public abstract int UFS_DecryptTemplate(Pointer paramPointer,
                                            byte[] paramArrayOfByte1,
                                            int paramInt,
                                            byte[] paramArrayOfByte2,
                                            IntByReference paramIntByReference);

    public abstract int UFS_GetCaptureImageBufferInfo(Pointer paramPointer,
                                                      IntByReference paramIntByReference1,
                                                      IntByReference paramIntByReference2,
                                                      IntByReference paramIntByReference3);

    public abstract int UFS_GetCaptureImageBuffer(Pointer paramPointer,
                                                  byte[] paramArrayOfByte);

    public abstract int UFS_SaveCaptureImageBufferToBMP(Pointer paramPointer,
                                                        String paramString);

    public abstract int UFS_SaveCaptureImageBufferTo19794_4(Pointer paramPointer,
                                                            String paramString);

    public abstract int UFS_ClearCaptureImageBuffer(Pointer paramPointer);

    public abstract int UFS_SaveCaptureImageBufferToWSQ(Pointer paramPointer,
                                                        float paramFloat,
                                                        String paramString);

    public abstract int UFS_GetErrorString(int paramInt,
                                           byte[] paramArrayOfByte);

    public abstract int UFS_GetTemplateType(Pointer paramPointer,
                                            IntByReference paramIntByReference);

    public abstract int UFS_SetTemplateType(Pointer paramPointer,
                                            int paramInt);

    public abstract int UFS_SelectTemplate_J(Pointer paramPointer,
                                             PointerByReference paramPointerByReference1,
                                             int[] paramArrayOfInt1,
                                             int paramInt1,
                                             PointerByReference paramPointerByReference2,
                                             int[] paramArrayOfInt2,
                                             int paramInt2);

    public abstract int UFS_SelectTemplateEx_J(Pointer paramPointer,
                                               int paramInt1,
                                               PointerByReference paramPointerByReference1,
                                               int[] paramArrayOfInt1,
                                               int paramInt2,
                                               PointerByReference paramPointerByReference2,
                                               int[] paramArrayOfInt2,
                                               int paramInt3);

    public static abstract interface UFS_CAPTURE_PROC
        extends Callback
    {

        public abstract int callback(Pointer paramPointer1,
                                     int paramInt1,
                                     Pointer paramPointer2,
                                     int paramInt2,
                                     int paramInt3,
                                     int paramInt4,
                                     PointerByReference paramPointerByReference);
    }

    public static abstract interface UFS_SCANNER_PROC
        extends Callback, StdCall
    {

        public abstract int callback(String paramString,
                                     int paramInt,
                                     PointerByReference paramPointerByReference);
    }
}
