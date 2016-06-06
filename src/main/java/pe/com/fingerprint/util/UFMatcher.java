package pe.com.fingerprint.util;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public abstract interface UFMatcher
    extends StdCallLibrary
{

    public static final int UFMOK = 0;
    public static final int UFMERROR = -1;
    public static final int UFMERR_NO_LICENSE = -101;
    public static final int UFMERR_LICENSE_NOT_MATCH = -102;
    public static final int UFMERR_LICENSE_EXPIRED = -103;
    public static final int UFMERR_NOT_SUPPORTED = -111;
    public static final int UFMERR_INVALID_PARAMETERS = -112;
    public static final int UFM_ERR_MATCH_TIMEOUT = -401;
    public static final int UFM_ERR_MATCH_ABORTED = -402;
    public static final int UFM_ERR_TEMPLATE_TYPE = -411;
    public static final int UFM_PARAM_FAST_MODE = 301;
    public static final int UFM_PARAM_SECURITY_LEVEL = 302;
    public static final int UFM_PARAM_USE_SIF = 311;
    public static final int UFM_TEMPLATE_TYPE_SUPREMA = 2001;
    public static final int UFM_TEMPLATE_TYPE_ISO19794_2 = 2002;
    public static final int UFM_TEMPLATE_TYPE_ANSI378 = 2003;

    public abstract int UFM_Create(PointerByReference paramPointerByReference);

    public abstract int UFM_Delete(Pointer paramPointer);

    public abstract int UFM_GetParameter(Pointer paramPointer,
                                         int paramInt,
                                         IntByReference paramIntByReference);

    public abstract int UFM_SetParameter(Pointer paramPointer,
                                         int paramInt,
                                         IntByReference paramIntByReference);

    public abstract int UFM_Verify(Pointer paramPointer,
                                   byte[] paramArrayOfByte1,
                                   int paramInt1,
                                   byte[] paramArrayOfByte2,
                                   int paramInt2,
                                   IntByReference paramIntByReference);

    public abstract int UFM_Identify_J(Pointer paramPointer,
                                       byte[] paramArrayOfByte,
                                       int paramInt1,
                                       PointerByReference paramPointerByReference,
                                       int[] paramArrayOfInt,
                                       int paramInt2,
                                       int paramInt3,
                                       IntByReference paramIntByReference);

    public abstract int UFM_IdentifyMT_J(Pointer paramPointer,
                                         byte[] paramArrayOfByte,
                                         int paramInt1,
                                         PointerByReference paramPointerByReference,
                                         int[] paramArrayOfInt,
                                         int paramInt2,
                                         int paramInt3,
                                         IntByReference paramIntByReference);

    public abstract int UFM_AbortIdentify(Pointer paramPointer);

    public abstract int UFM_IdentifyInit(Pointer paramPointer,
                                         byte[] paramArrayOfByte,
                                         int paramInt);

    public abstract int UFM_IdentifyNext(Pointer paramPointer,
                                         byte[] paramArrayOfByte,
                                         int paramInt,
                                         IntByReference paramIntByReference);

    public abstract int UFM_RotateTemplate(Pointer paramPointer,
                                           byte[] paramArrayOfByte,
                                           int paramInt);

    public abstract int UFM_GetErrorString(int paramInt,
                                           byte[] paramArrayOfByte);

    public abstract int UFM_GetTemplateType(Pointer paramPointer,
                                            IntByReference paramIntByReference);

    public abstract int UFM_SetTemplateType(Pointer paramPointer,
                                            int paramInt);
}
