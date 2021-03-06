package z.hol.net.download.app;

import z.hol.model.SimpleApp;
import z.hol.net.download.AbsDownloadManager.Task;
import z.hol.net.download.ContinuinglyDownloader;

public class AppContinuinglyDownloader extends ContinuinglyDownloader{
	
	private DownloadListener mListener;
	private SimpleApp mApp;
	private AppStatusSaver mStatusSaver;
	
	public AppContinuinglyDownloader(SimpleApp app,String saveFile, long startPos, AppStatusSaver saver, DownloadListener listener){
		super(app.getAppUrl(), app.getSize(), startPos, 0, saveFile);
		mApp = app;
		mListener = listener;
		mStatusSaver = saver;
		if (mStatusSaver == null){
			//mFileService = new FileService();
			throw new IllegalArgumentException("file service is null, I can not save download state.");
		}
	}
	
	public AppContinuinglyDownloader(String url, long blockSize,
			long startPos, int threadIndex, String filePath) {
		super(url, blockSize, startPos, threadIndex, filePath);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取下载ID<br>
	 * 一般是TaskID
	 * @return
	 */
	public long getDownloadId(){
		return mApp.getAppId();
	}
	
	@Override
	public void onRedirect(String originUrl, String newUrl) {
		// TODO Auto-generated method stub
		super.onRedirect(originUrl, newUrl);
		mStatusSaver.changUrl(getDownloadId(), newUrl);
	}

	@Override
	protected boolean isAleadyComplete(long startPos, long remain,
			long blockSize) {
		// TODO Auto-generated method stub
		//return super.isAleadyComplete(startPos, remain, blockSize);
		if (startPos == blockSize){
			return true;
		}
		return false;
	}
	
	@Override
	protected void onPerpareFileSizeDone(long total) {
		// TODO Auto-generated method stub
		super.onPerpareFileSizeDone(total);
		mStatusSaver.updateAppSize(getDownloadId(), total);
	}
	
	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub
		super.onPrepare();
		mStatusSaver.changeAppTaskState(getDownloadId(), Task.STATE_PERPARE);
		if (mListener != null){
			mListener.onPrepare(getDownloadId());
		}
	}

	@Override
	protected void onStart(long startPos, long remain, long blockSize) {
		// TODO Auto-generated method stub
		super.onStart(startPos, remain, blockSize);
		mStatusSaver.changeAppTaskState(getDownloadId(), Task.STATE_RUNNING);
		if (mListener != null){
			mListener.onStart(getDownloadId(), blockSize, startPos);
		}
	}

	@Override
	protected void saveBreakpoint(long startPos, long remain, long blockSize) {
		// TODO Auto-generated method stub
		super.saveBreakpoint(startPos, remain, blockSize);
		mStatusSaver.updateAppDownloadPos(getDownloadId(), startPos);
		if (mListener != null){
			mListener.onProgress(getDownloadId(), blockSize, startPos);
		}
	}

	@Override
	protected void onBlockComplete() {
		// TODO Auto-generated method stub
		super.onBlockComplete();
		mStatusSaver.changeAppTaskState(getDownloadId(), Task.STATE_COMPLETE);
		if (mListener != null){
			mListener.onComplete(getDownloadId());
		}
	}

	@Override
	protected void onDownloadError(int errorCode) {
		// TODO Auto-generated method stub
		super.onDownloadError(errorCode);
		mStatusSaver.changeAppTaskState(getDownloadId(), Task.STATE_PAUSE);
		if (mListener != null){
			mListener.onError(getDownloadId(), errorCode);
		}
	}

	@Override
	protected void onCancel() {
		// TODO Auto-generated method stub
		super.onCancel();
		mStatusSaver.changeAppTaskState(getDownloadId(), Task.STATE_PAUSE);
		if (mListener != null){
			mListener.onCancel(getDownloadId());
		}
	}

}
