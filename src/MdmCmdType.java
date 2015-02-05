package org.wzq.android.mdm;

enum MdmCmdType {
    password_reset("password_reset"), //mx2 fail  u8860 ok
    password_min_len("password_min_len"), // mx2 fail  U8860 ok
    password_quality("password_quality"), // mx2 fail  u8860 ok
    password_min_letter("password_min_letter"),//mx2 fail u8860 ok
    password_min_letter_low("password_min_letter_low"),//mx2 fail u8860 ok
    password_min_digit("password_min_digit"),//mx2 fail u8860 ok
    password_min_letter_cap("password_min_letter_cap"),//mx2 fail u8860 ok
    password_expire_time("password_expire_time"),//mx2 fail u8860 ok
    password_history_restrict("password_history_restrict"), // u8860无效
    password_max_fail("password_max_fail"),//ok 当魅族执行后3次失败 u8860未测试
    lock_screen("lock_screen"),//mx2 ok u8860 ok
    timeout_screen("timeout_screen"), //mx2 ok u8860 ok
    storage_crypt("storage_crypt"),// mx2 不支持 u8860 ok
    camera_disable("camera_disable"),//mx2 ok u8860 ok
    wipe_data("wipe_data"),// mx2 ok u8860未测试
    get_installed_applist("get_installed_applist"),  //mx2 ok u8860 ok

    //
    uninstall_app("uninstall_app"),//移除应用
    get_device_info("get_device_info"),//获取设备信息
    install_app("install_app"),//安装应用程序

    // added by wzq @ 2014-11-13
    password_min_symbol("password_min_symbol"),//密码最小符号长度
    device_config("device_config");// 手机配置
    //password_require("password_require")//如果没有密码，则要求设置

    private MdmCmdType(String s) {
        str = s;
    }

    private String str;

    public String toString() {
        return str;
    }
}
