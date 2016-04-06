package com.netease.carinsu.improve.constant;

import java.util.Map;

/**
 * @author lone
 * @date 2015/10/6
 * @time 11:23
 */
public enum Ret {

    OK(200,"OK"),
    NOT_STARTED(300,"投保流程尚未开始"),
    RECEIVED_GIFT(301,"您已经领取过优惠券!"),
    QUOTE_INFO_LOSE(302,"报价信息丢失，请重新投保"),
    VERIFY_ERROR(303,"验证码校验失败"),
    CHECK_ERROR(304,"核保失败"),
    PA_ERROR(305,"平安接口失败"),
    QUOTE_ERROR(306,"报价失败"),
    VEHICLE_ERRPR(307,"无法获取到车型信息"),
    WRONG_PARAMETER(401,"参数错误"),
    VEHICLE_ERROR(402,"无法获取到本地车型"),
    ERROR(500,"系统错误,请稍后重试"),
    NOT_LOGIN(501,"请您先登录"),
    NOT_BIND(502,"请绑定网易账号"),
    ACCOUNT_CHANGED(503,"登录的账号与原使用账号不一致"),
    SIMULATION_NOT_ALIVE(600,"投保流程异常,请重新尝试投保"),
    SIMULATION_START_ERROR(601,"无法为您开始投保流程"),
    SIMULATIONS_DATA_ERROR(602,"获取信息失败,请稍后重试"),
    SIMULATIONS_TIME_OUT(603,"等待超时"),
    SIMULATION_OWNER_NAME_ERROR(1,"车主姓名错误"),
    SIMULATION_CAPTCHA_ERROR(3,"图片验证码错误"),
    SIMULATION_LICENSE_ERROR(4,"车架号错误"),
    SIMULATION_ENGINE_NO_ERROR(5,"发动机号错误"),
    SIMULATION_ENROLl_DATE_ERROR(6,"车辆初始登记日期错误"),
    SIMULATION_BJ_SMS_VERIFY_ERROR(7,"北京地区核保前短信验证码错误"),
    SIMULATION_OWNER_ID_ERROR(8,"车主证件号码错误"),
    SIMULATION_INSURED_DATE_ERROR(9,"您的车险距到期日还有很久哦，请于到期前60天内再来投保，人保车险随时恭候您！"),
    SIMULATION_FAIL_MAX_TIME_ERROR(10,"数据接口失败达到最大次数"),
    SIMULATION_STEP_MAX_TIME_ERROR(11,"步骤接口失败达到最大次数"),
    SIMULATION_CAPACHA_CRACK_ERROR(12,"图片验证码破解接口失败达到最大次数"),
    SIMULATION_FOR_START_DATE_ERROR(13,"交强险保单生效日期有误"),
    SIMULATION_OWNER_MOBILE_ERROR(14,"车主手机号不正确"),
    SIMULATION_OWNER_LICENSE_NO_ERROR(15,"车牌号不正确"),
    SIMULATION_VIN_ERROR(16,"VIN码不正确"),
    SIMULATION_CAP_ERROR(17,"核定载客量不正确"),
    SIMULATION_OWNER_MAIL_ERROR(18,"车主电子邮箱不正确"),
    SIMULATION_INSURED_NAME_ERROR(19,"被保险人姓名不正确"),
    SIMULATION_INSURED_ID_ERROR(20,"被保险人证件号码不正确"),
    SIMULATION_INSURED_MOBILE_ERROR(21,"被保险人手机号不正确"),
    SIMULATION_INSURED_MAIL_ERROR(22,"被保险人电子邮箱不正确"),
    SIMULATION_APPLICANT_NAME_ERROR(23,"投保人姓名不正确"),
    SIMULATION_APPLICANT_ID_ERROR(24,"投保人证件号码不正确"),
    SIMULATION_APPLICANT_PHONE_ERROR(25,"投保人手机号不正确"),
    SIMULATION_APPLICANT_MAIL_ERROR(26,"投保人电子邮箱不正确"),
    SIMULATION_DELIVER_NAME_ERROR(27,"保单寄送姓名不正确"),
    SIMULATION_DELIVER_PHONE_ERROR(28,"保单寄送手机号不正确"),
    SIMULATION_DELIVER_AREA_ERROR(29,"保单寄送地区不正确"),
    SIMULATION_DELIVER_ADDRESS_ERROR(30,"保单寄送地址不正确"),
    SIMULATION_INVOICE_ERROR(31,"保单寄送发票抬头不正确"),
    SIMULATION_VEHICLE_ERROR(32,"车型库中找不到该车型"),
    SIMULATION_DATE_ERROR(33,"您的车辆上年交强险终保日期为2016-03-19，此次投保的最早有效日期为2016-03-20。由于网站暂不支持商业险与交强险分期起保，请您修改商业险的起保日期或不购买交强险"),
    SIMULATION_V_ERROR(34,"被保险人需与车主一致"),
    SIMULATION_DATE_ERROR2(35,"您的车辆上年交强险终保日期为2016-03-19，此次投保的最早有效日期为2016-03-20"),
    SIMULATION_AUDIT_ERROR(36,"您的保单尚未通过审核，请稍候再次进行投保。如有疑问请拨打客服电话400-1234567"),
    SIMULATION_INPUT_APP_MOBILD(37,"请输入其它的投保人手机号"),
    SIMULATION_QUOTE_DISABLED(38,"您的车辆暂时不能在网上进行报价，请到当地营业厅进行查询"),
    SIMULATION_QUOTE_VEHICLE_ERROR(39,"您输入的车辆信息在当地保险公共平台无匹配记录"),
    SIMULATION_OWNER_BIRTH_ERROR(40,"车主生日有误"),
    SIMULATION_DATE_ERROR3(41,"您上年的商业险保单终保日期为****年**月**日，请您修改今年保单的生效日期！"),
    SIMULATION_VEHICLE_ERROR2(42,"对不起,您输入的车辆信息在当地保险公共平台无匹配记录 , 请核对后重新输入。"),
    SIMULATION_NETWORK_TIMEOUT(43,"链接平台超时"),
    SIMULATION_INSURED_ID_ADDR_ERROR(44,"被保险人证件地址不正确"),
    SIMULATION_PLATFORM_ERROR(45,"平台连接异常，请稍候重试！"),
    SIMULATION_INSURE_NOT_AUTH(46,"尊敬的客户，您好。您的保单未通过审核，原因为:1、您的车辆投保三责险，保额需大于20万（含）;如有疑问请拨打客服电话400-1234567转2。"),
    SIMULATION_INPUT_TIMEOUT(47,"等待用户输入、修改或提交表单超时"),
    SIMULATION_UNKONWN_ERROR(99,"未知错误");




    Ret(int retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }
    final int retCode;
    final String retMsg;
    public static final String RET_CODE_KEY="retCode";
    public static final String RET_MSG_KEY="retMsg" ;

    public int getRetCode() {
        return retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public static Ret getRetByCode(int retCode) {
        for(Ret ret : Ret.values()) {
            if(ret.getRetCode() == retCode) {
                return ret;
            }
        }
        return null;
    }

    public static void setRet(Map retMap,Ret ret) {
        if(retMap == null || ret == null) {
            return;
        }
        retMap.put(RET_CODE_KEY,ret.retCode);
        retMap.put(RET_MSG_KEY,ret.retMsg);
    }
}
