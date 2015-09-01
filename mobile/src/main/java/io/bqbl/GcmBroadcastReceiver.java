public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      // Explicitly specify that GcmMessageHandler will handle the intent.
      ComponentName component = new ComponentName(context.getPackageName(),
          GcmMessageHandler.class.getName());

      // Start the service, keeping the device awake while it is launching.
      startWakefulService(context, (intent.setComponent(component)));
      setResultCode(Activity.RESULT_OK);
  }
}
