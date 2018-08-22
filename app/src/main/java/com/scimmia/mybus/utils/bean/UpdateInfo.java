package com.scimmia.mybus.utils.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by lk on 2017/8/26.
 */
public class UpdateInfo {
    int versionCode;
    String versionName;
    String softUrl;
    String updateLog;
    String fileSize;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getSoftUrl() {
        return softUrl;
    }

    public void setSoftUrl(String softUrl) {
        this.softUrl = softUrl;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    //
//    /**
//     * code : 0
//     * message :
//     * data : [{"appKey":"17563cb03a3cdc8d8aef708bd2cc3939","appType":"2","appIsLastest":"1","appFileName":"myyantaibus_legu_signed_zipalign.apk","appFileSize":"3978083","appName":"烟台公交定制版","appVersion":"1.0","appVersionNo":"1","appBuildVersion":"1","appIdentifier":"com.scimmia.mybus","appIcon":"afed2bdd797e0f2ea6e4c8d6aec8f78d","appDescription":"小明是个公交族，每天从南洪街乘坐2路或5路到三站上班。偶尔遇上堵车2路5路都不来，只好坐上1路下车多走一段，或者忍痛登上K62...\n自从用了\u201c烟台公交\u201dapp后，小明可以结合\u201c高德地图\u201d的实时路况，掌握公交车的位置信息，生活有了很大的提高，可是打开这个看看位置信息切换到那个看看路况信息往往有些手忙脚乱。\n如果能把二者结合起来就好了。","appUpdateDescription":"","appScreenshots":"7515d026632054cb7b3fd42c2163e174,a2e73747b990e7131acfda207aced3fd,081524d8120c97ac4501838f2c549e80,95573b37412d33feb97f750954396956,d28ffc737de533e59e9feb332d785ea0","appShortcutUrl":"TMWz","appCreated":"2017-08-24 16:29:57","appUpdated":"2017-08-24 16:29:57","appQRCodeURL":"http://www.pgyer.com/app/qrcodeHistory/57e5037786d62125eeaa4f17d11d3496e19b582af9457948a1ee17f744a015a0"}]
//     */
//
//    public DataBean getMaxVersion(){
//        if (data!=null){
//            for (DataBean temp :
//                    data) {
//                if (StringUtils.equals(temp.getAppIsLastest(),"1")){
//                    return temp;
//                }
//            }
//        }
//        return null;
//    }
//
//    private int code;
//    private String message;
//    private List<DataBean> data;
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public List<DataBean> getData() {
//        return data;
//    }
//
//    public void setData(List<DataBean> data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * appKey : 17563cb03a3cdc8d8aef708bd2cc3939
//         * appType : 2
//         * appIsLastest : 1
//         * appFileName : myyantaibus_legu_signed_zipalign.apk
//         * appFileSize : 3978083
//         * appName : 烟台公交定制版
//         * appVersion : 1.0
//         * appVersionNo : 1
//         * appBuildVersion : 1
//         * appIdentifier : com.scimmia.mybus
//         * appIcon : afed2bdd797e0f2ea6e4c8d6aec8f78d
//         * appDescription : 小明是个公交族，每天从南洪街乘坐2路或5路到三站上班。偶尔遇上堵车2路5路都不来，只好坐上1路下车多走一段，或者忍痛登上K62...
//         自从用了“烟台公交”app后，小明可以结合“高德地图”的实时路况，掌握公交车的位置信息，生活有了很大的提高，可是打开这个看看位置信息切换到那个看看路况信息往往有些手忙脚乱。
//         如果能把二者结合起来就好了。
//         * appUpdateDescription :
//         * appScreenshots : 7515d026632054cb7b3fd42c2163e174,a2e73747b990e7131acfda207aced3fd,081524d8120c97ac4501838f2c549e80,95573b37412d33feb97f750954396956,d28ffc737de533e59e9feb332d785ea0
//         * appShortcutUrl : TMWz
//         * appCreated : 2017-08-24 16:29:57
//         * appUpdated : 2017-08-24 16:29:57
//         * appQRCodeURL : http://www.pgyer.com/app/qrcodeHistory/57e5037786d62125eeaa4f17d11d3496e19b582af9457948a1ee17f744a015a0
//         */
//
//        private String appKey;
//        private String appType;
//        private String appIsLastest;
//        private String appFileName;
//        private String appFileSize;
//        private String appName;
//        private String appVersion;
//        private String appVersionNo;
//        private String appBuildVersion;
//        private String appIdentifier;
//        private String appIcon;
//        private String appDescription;
//        private String appUpdateDescription;
//        private String appScreenshots;
//        private String appShortcutUrl;
//        private String appCreated;
//        private String appUpdated;
//        private String appQRCodeURL;
//
//        public String getAppKey() {
//            return appKey;
//        }
//
//        public void setAppKey(String appKey) {
//            this.appKey = appKey;
//        }
//
//        public String getAppType() {
//            return appType;
//        }
//
//        public void setAppType(String appType) {
//            this.appType = appType;
//        }
//
//        public String getAppIsLastest() {
//            return appIsLastest;
//        }
//
//        public void setAppIsLastest(String appIsLastest) {
//            this.appIsLastest = appIsLastest;
//        }
//
//        public String getAppFileName() {
//            return appFileName;
//        }
//
//        public void setAppFileName(String appFileName) {
//            this.appFileName = appFileName;
//        }
//
//        public String getAppFileSize() {
//            return appFileSize;
//        }
//
//        public void setAppFileSize(String appFileSize) {
//            this.appFileSize = appFileSize;
//        }
//
//        public String getAppName() {
//            return appName;
//        }
//
//        public void setAppName(String appName) {
//            this.appName = appName;
//        }
//
//        public String getAppVersion() {
//            return appVersion;
//        }
//
//        public void setAppVersion(String appVersion) {
//            this.appVersion = appVersion;
//        }
//
//        public String getAppVersionNo() {
//            return appVersionNo;
//        }
//
//        public void setAppVersionNo(String appVersionNo) {
//            this.appVersionNo = appVersionNo;
//        }
//
//        public String getAppBuildVersion() {
//            return appBuildVersion;
//        }
//
//        public void setAppBuildVersion(String appBuildVersion) {
//            this.appBuildVersion = appBuildVersion;
//        }
//
//        public String getAppIdentifier() {
//            return appIdentifier;
//        }
//
//        public void setAppIdentifier(String appIdentifier) {
//            this.appIdentifier = appIdentifier;
//        }
//
//        public String getAppIcon() {
//            return appIcon;
//        }
//
//        public void setAppIcon(String appIcon) {
//            this.appIcon = appIcon;
//        }
//
//        public String getAppDescription() {
//            return appDescription;
//        }
//
//        public void setAppDescription(String appDescription) {
//            this.appDescription = appDescription;
//        }
//
//        public String getAppUpdateDescription() {
//            return appUpdateDescription;
//        }
//
//        public void setAppUpdateDescription(String appUpdateDescription) {
//            this.appUpdateDescription = appUpdateDescription;
//        }
//
//        public String getAppScreenshots() {
//            return appScreenshots;
//        }
//
//        public void setAppScreenshots(String appScreenshots) {
//            this.appScreenshots = appScreenshots;
//        }
//
//        public String getAppShortcutUrl() {
//            return appShortcutUrl;
//        }
//
//        public void setAppShortcutUrl(String appShortcutUrl) {
//            this.appShortcutUrl = appShortcutUrl;
//        }
//
//        public String getAppCreated() {
//            return appCreated;
//        }
//
//        public void setAppCreated(String appCreated) {
//            this.appCreated = appCreated;
//        }
//
//        public String getAppUpdated() {
//            return appUpdated;
//        }
//
//        public void setAppUpdated(String appUpdated) {
//            this.appUpdated = appUpdated;
//        }
//
//        public String getAppQRCodeURL() {
//            return appQRCodeURL;
//        }
//
//        public void setAppQRCodeURL(String appQRCodeURL) {
//            this.appQRCodeURL = appQRCodeURL;
//        }
//    }
}
