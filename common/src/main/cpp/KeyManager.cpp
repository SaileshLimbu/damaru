#include <jni.h>
#include <jni.h>
#include <string>

//DECLARATIONS
const std::string secretKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv6IZoHMWrkhUSQo5IUoCvpayLqcoGKj8Hnnx7HU+q8ia2n9Pqo163sN1a3aWJOqu34lErPehRDbdcOA5E4kQ965pRoUxUW2uf9Kf8pAn1erDblQ7Ohpq+CUg3/47eqENBLfP3eF2H2DAlAOTW2qbJDtb5dh5+jYoBMVH+NJrpJsM7AZQAvPmaGmNpl8fR3K7TnjRBTIAwhX5PVyVvYoiLZkO/fQQ0HnkZP/O2XwTQ5V4Xlnjw4lgYS800Wf0VGyrwEqzrFK+FrHUu/NLLtgqZpFyDVo8IlV0fsvfYZTONeK4eR8eQA72Im7N/3tMwpny+0Su6Jscp3BI7EkUP5TQjQIDAQAB-----END PUBLIC KEY-----";

extern "C" JNIEXPORT jstring
JNICALL
Java_com_powersoft_common_utils_SoManager_getPublicKey(JNIEnv *env, jclass object) {
    return env->NewStringUTF(secretKey.c_str());
}
