package com.scimmia.mybus.utils.http;

/**
 * Created by A on 2016/8/25.
 */
public interface HttpListener {
    public void onSuccess(String tag, String content);

    /*

                            if (StringUtils.isEmpty(content)) {
                                showToast("网络连接错误，请稍后重试。");
                            } else {
                                BaseBean bean = new Gson().fromJson(content, BaseBean.class);
                                if (bean.getResponse_code() != 1) {
                                    showToast(bean.getShow_err());
                                } else {
                                    showToast(bean.getShow_err());
                                }
                            }
    * */
}
