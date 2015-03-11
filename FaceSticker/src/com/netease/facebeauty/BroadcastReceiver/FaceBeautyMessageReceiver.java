/**  
 * @Project: FaceBeautyDemo
 * @Title: FaceBeautyMessageReceiver.java
 * @Package com.netease.facebeauty.BroadcastReceiver
 * @author Young
 * @date 2015年2月12日 下午4:16:12
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.BroadcastReceiver;


/**
 * FaceBeautyMessageReceiver
 * 
 * @author Young</br> 2015年2月12日 下午4:16:12
 */
public class FaceBeautyMessageReceiver
//extends PushMessageReceiver 
{

//    @Override
//    public void onReceiveMessage(Context context, MiPushMessage message) {
//        Log.v(FaceBeautyApplication.TAG, "onReceiveMessage is called. " + message.toString());
//        String log = context.getString(R.string.recv_message, message.getContent());
//        MainActivity.logList.add(0, getSimpleDate() + " " + log);
//
//        Message msg = Message.obtain();
//        if (message.isNotified()) {
//            msg.obj = log;
//        }
//        // FaceBeautyApplication.getHandler().sendMessage(msg);
//    }
//
//    @Override
//    public void onCommandResult(Context context, MiPushCommandMessage message) {
//        Log.v(FaceBeautyApplication.TAG, "onCommandResult is called. " + message.toString());
//        String command = message.getCommand();
//        List<String> arguments = message.getCommandArguments();
//        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
//        String log = "";
//        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.register_success);
//            } else {
//                log = context.getString(R.string.register_fail);
//            }
//        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.set_alias_success, cmdArg1);
//            } else {
//                log = context.getString(R.string.set_alias_fail, message.getReason());
//            }
//        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.unset_alias_success, cmdArg1);
//            } else {
//                log = context.getString(R.string.unset_alias_fail, message.getReason());
//            }
//        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.subscribe_topic_success, cmdArg1);
//            } else {
//                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
//            }
//        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.unsubscribe_topic_success, cmdArg1);
//            } else {
//                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
//            }
//        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.set_accept_time_success, cmdArg1, cmdArg2);
//
//                Message msg = Message.obtain();
//                msg.what = 1;
//                msg.arg1 = (cmdArg1.equals(cmdArg2)) ? 0 : 1;
//                // FaceBeautyApplication.getHandler().sendMessage(msg);
//            } else {
//                log = context.getString(R.string.set_accept_time_fail, message.getReason());
//            }
//        } else {
//            log = message.getReason();
//        }
//        MainActivity.logList.add(0, getSimpleDate() + "    " + log);
//
//        Message msg = Message.obtain();
//        msg.obj = log;
//        // FaceBeautyApplication.getHandler().sendMessage(msg);
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    public static String getSimpleDate() {
//        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
//    }
//
//    public static class FaceBeautyHandler extends Handler {
//
//        private Context context;
//
//        public FaceBeautyHandler(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            String s = (String) msg.obj;
//
//            if (MainActivity.sMainActivity != null) {
//                MainActivity.sMainActivity.refreshLogInfo();
//
//                if (msg.what == 1) {
//                    return;
//                    // MainActivity.sMainActivity.refreshPushState((msg.arg1 ==
//                    // 1) ? true : false);
//                }
//            }
//            if (!TextUtils.isEmpty(s)) {
//                // Toast.makeText(context, s, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
