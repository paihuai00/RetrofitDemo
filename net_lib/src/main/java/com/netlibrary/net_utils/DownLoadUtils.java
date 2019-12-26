package com.netlibrary.net_utils;

import android.support.annotation.WorkerThread;
import com.netlibrary.impls.DownLoadImpl;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;

/**
 * Date: 2019/12/23
 * create by cuishuxiang
 * description: Retrofit 下载Utils
 *
 * 回调{@link DownLoadImpl}
 */
public class DownLoadUtils {
    private static final String TAG = "DownLoadUtils";

    private static CompositeDisposable mCompositeDisposable;

    /**
     * 下载方法
     * @param observable 由于下载文件，文件可能过大，将保存到sd卡操作放到 子线程；所以进度回调也在子线程
     * @param completeFileName
     * @param downLoad 下载回调
     */
    public static void downLoad(Observable<ResponseBody> observable, final String completeFileName,
            final DownLoadImpl downLoad) {
        Disposable disposable =
                observable.subscribeOn(Schedulers.io()).map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        //System.out.println("aaa apply  Thread.currentThread().getName() = " + Thread.currentThread().getName());
                        return writeFile2Disk(responseBody, completeFileName, downLoad);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        downLoad.onDownLoadFinish(file);
                        NetLogUtil.d(TAG + " 下载文件成功 ：file.getName() =  " + file.getName()+" file.getAbsolutePath() = "+file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        downLoad.onDownLoadFailed(throwable.getMessage());
                        NetLogUtil.d(TAG +"文件： "+completeFileName+ " 下载文件失败 ：throwable.getMessage() =  " + throwable.getMessage());
                    }
                });

        if (mCompositeDisposable==null) mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(disposable);
    }

    public static void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    @WorkerThread
    private static File writeFile2Disk(ResponseBody responseBody, String fileName,
            DownLoadImpl downloadListener) {
        //创建一个 File
        File file = new File(fileName);
        if (file.exists()) file.delete();// 如果文件存在，直接删除

        long currentLength = 0;
        OutputStream os = null;

        if (responseBody == null) {
            downloadListener.onDownLoadFailed("资源错误！");
            return file;
        }
        InputStream is = responseBody.byteStream();
        long totalLength = responseBody.contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                NetLogUtil.d(TAG + "当前进度: " + currentLength);
                downloadListener.onProgressCallBack((int) (100 * currentLength / totalLength));
                if ((int) (100 * currentLength / totalLength) == 100) {
                    downloadListener.onDownLoadFinish(file);
                }
            }
        } catch (FileNotFoundException e) {
            downloadListener.onDownLoadFailed("未找到文件！");
            e.printStackTrace();
        } catch (IOException e) {
            downloadListener.onDownLoadFailed("IO错误！");
            NetLogUtil.d(TAG + "IO错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return file;
        }
    }
}
