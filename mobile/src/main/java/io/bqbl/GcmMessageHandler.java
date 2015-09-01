public class GcmMessageHandler extends IntentService {
    
  @Override
  protected void onHandleIntent(Intent intent) {
    mIntent = intent;
    showToast();
    Application application = (Application) getApplication();

    try {
      URL url = new URL(BuildConfig.SYNC_URL_HTTP);
      Replication pull = application.getDatabase().createPullReplication(url);
      pull.addChangeListener(getReplicationListener());
      pull.start();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  private Replication.ChangeListener getReplicationListener() {
    return new Replication.ChangeListener() {
      @Override
      public void changed(Replication.ChangeEvent event) {
        Log.i("GCM", "replication status is : " + event.getSource().getStatus());              if (event.getSource().getStatus() == Replication.ReplicationStatus.REPLICATION_STOPPED) {
            GcmBroadcastReceiver.completeWakefulIntent(mIntent);
        }
      }
    };
  }
}
