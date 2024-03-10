package arvolear.zoomer.zoomer.expansions;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Messenger;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import java.io.File;

import arvolear.zoomer.zoomer.MainActivity;
import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.menu.MenuActivity;

public class ExpansionController implements View.OnClickListener, IDownloaderClient
{
    private AppCompatActivity activity;

    public static final boolean EXP_IS_MAIN = true;
    public static final int EXP_VERSION = 7;
    public static final long EXP_SIZE = 159356979;

    public static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final int PERMISSION_CODE = 1;

    private boolean statePaused = false;
    private boolean cellularShown = false;
    private int state;

    private IStub downloaderClientStub;
    private IDownloaderService remoteService;

    private ExpansionBackgroundSetter expansionBackgroundSetter;
    private ExpansionPage expansionPage;

    public ExpansionController(AppCompatActivity activity)
    {
        this.activity = activity;
    }

    private void initUI()
    {
        downloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ExpansionDownloaderService.class);

        activity.setContentView(R.layout.download);

        expansionBackgroundSetter = new ExpansionBackgroundSetter(activity, "textures/expansions/background", 6000, 12000);
        expansionPage = new ExpansionPage(activity, this);
    }

    public boolean checkPermission()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(activity, PERMISSION) == PackageManager.PERMISSION_DENIED)
        {
            File obb = new File(Helpers.getExpansionAPKFileName(activity, EXP_IS_MAIN, EXP_VERSION));

            if (!obb.canRead())
            {
                ActivityCompat.requestPermissions(activity, new String[]{PERMISSION}, PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    private void checkExpansions()
    {
        File packageFile = activity.getObbDir();

        if (!packageFile.exists())
        {
            packageFile.mkdirs();
        }
        else
        {
            File[] expansionFiles = packageFile.listFiles();

            if (expansionFiles != null)
            {
                String expName = Helpers.getExpansionAPKFileName(activity, EXP_IS_MAIN, EXP_VERSION);

                for (File file : expansionFiles)
                {
                    if (!file.getName().equals(expName))
                    {
                        file.delete();
                    }
                }
            }
        }
    }

    private boolean expansionFilesDelivered()
    {
        checkExpansions();

        String expName = Helpers.getExpansionAPKFileName(activity, EXP_IS_MAIN, EXP_VERSION);

        return Helpers.doesFileExist(activity, expName, EXP_SIZE, true);
    }

    public boolean downloadContent()
    {
        if (!expansionFilesDelivered())
        {
            Intent notifierIntent = new Intent(activity, MainActivity.class);
            notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.getActivity(activity, 0, notifierIntent, PendingIntent.FLAG_MUTABLE);
            }
            else
            {
                PendingIntent.getActivity(activity, 0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            try
            {
                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(activity, pendingIntent, ExpansionDownloaderService.class);

                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED)
                {
                    initUI();
                    return true;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return false;
    }

    private void launchTheGame()
    {
        Thread launchThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception ex)
                {
                }

                Intent menuIntent = new Intent(activity, MenuActivity.class);

                activity.startActivity(menuIntent);
                activity.overridePendingTransition(R.anim.expansions_to_menu_alpha_up, R.anim.expansions_to_menu_alpha_down);

                activity.finish();
            }
        });

        launchThread.start();
    }

    public void start()
    {
        if (downloaderClientStub != null)
        {
            downloaderClientStub.connect(activity);

            expansionBackgroundSetter.start();
            expansionPage.start();
        }
    }

    public void stop()
    {
        if (downloaderClientStub != null)
        {
            downloaderClientStub.disconnect(activity);

            expansionBackgroundSetter.stop();
            expansionPage.stop();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (cellularShown)
        {
            if (v.getId() == expansionPage.getCellularResumeButtonId())
            {
                remoteService.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
                remoteService.requestContinueDownload();
            }
            else if (v.getId() == expansionPage.getCellularCancelButtonId())
            {
                cellularShown = false;

                expansionPage.waitForWifi();
                expansionPage.triggerCellular(cellularShown);
            }
        }
        else if (v.getId() == expansionPage.getResumePauseButtonId())
        {
            statePaused = !statePaused;

            if (statePaused)
            {
                remoteService.requestPauseDownload();
            }
            else
            {
                remoteService.requestContinueDownload();
            }

            expansionPage.updateResumePauseButton(statePaused);
        }
    }

    @Override
    public void onServiceConnected(Messenger m)
    {
        remoteService = DownloaderServiceMarshaller.CreateProxy(m);
        remoteService.onClientUpdated(downloaderClientStub.getMessenger());
    }

    @Override
    public void onDownloadStateChanged(int stateId)
    {
        boolean showCellMessage = false;
        boolean paused = false;
        boolean indeterminate = false;

        switch (stateId)
        {
            case IDownloaderClient.STATE_IDLE:
            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                indeterminate = true;
                break;

            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                paused = true;
                break;

            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                paused = true;
                showCellMessage = true;
                break;

            case IDownloaderClient.STATE_DOWNLOADING:
                break;

            case IDownloaderClient.STATE_COMPLETED:
                expansionPage.setFinished();
                launchTheGame();
                break;

            default:
                paused = true;
                indeterminate = true;
        }

        state = stateId;
        statePaused = paused;

        if (cellularShown != showCellMessage)
        {
            cellularShown = showCellMessage;
            expansionPage.triggerCellular(cellularShown);
        }

        expansionPage.triggerIndeterminate(indeterminate);

        expansionPage.updateState(state);
        expansionPage.updateResumePauseButton(statePaused);
    }

    @Override
    public void onDownloadProgress(DownloadProgressInfo progress)
    {
        expansionPage.updateProgress(progress);
    }
}
