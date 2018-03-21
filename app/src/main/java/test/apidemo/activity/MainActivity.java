package test.apidemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    Context mContext;

    //ITEM icc
    private static final int ITEM_CODE_ICC = 0;
    //ITEM nfc
    private static final int ITEM_CODE_NFC = 1;
    //ITEM mcr
    private static final int ITEM_CODE_MCR = 2;
    //ITEM pci
    private static final int ITEM_CODE_PCI = 3;
    //ITEM print
    private static final int ITEM_CODE_PRINT = 4;
    //ITEM sys
    private static final int ITEM_CODE_SYS = 5;
    //ITEM Scan
    private static final int ITEM_CODE_SCAN = 6;
    //update
    private static final int ITEM_CODE_UPDATE_OS = 7;
    //emv
    private static final int ITEM_CODE_EMV = 8;

    // Used to load the 'native-lib' library on application startup.

    private GridMenuLayout mGridMenuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        mContext = MainActivity.this;

        initViews();

    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    private void initViews() {

        final Drawable[] itemImgs = {
                getResources().getDrawable(R.mipmap.icc),
                getResources().getDrawable(R.mipmap.nfc),
                getResources().getDrawable(R.mipmap.mcr),
                getResources().getDrawable(R.mipmap.pci),
                getResources().getDrawable(R.mipmap.print),
                getResources().getDrawable(R.mipmap.sys),
                getResources().getDrawable(R.mipmap.scan),
                getResources().getDrawable(R.mipmap.upgrade),
                getResources().getDrawable(R.mipmap.emv),
                getResources().getDrawable(R.mipmap.more)
        };

        final String[] itemTitles = {
                getString(R.string.icc)
                , getString(R.string.picc)
                , getString(R.string.mcr)
                , getString(R.string.pci)
                , getString(R.string.print)
                , getString(R.string.sys)
                , getString(R.string.scan)
                , getString(R.string.upgrade_os)
                , " Emv "
                ,getString(R.string.more)
        };

        final int sizeWidth = getResources().getDisplayMetrics().widthPixels / 25;

        mGridMenuLayout = (GridMenuLayout) findViewById(R.id.myGrid);
        mGridMenuLayout.setGridAdapter(new GridMenuLayout.GridAdapter() {

            @Override
            public View getView(int index) {
                View view = getLayoutInflater().inflate(R.layout.gridmenu_item, null);
                ImageView gridItemImg = (ImageView) view.findViewById(R.id.gridItemImg);
                TextView gridItemTxt = (TextView) view.findViewById(R.id.gridItemTxt);

                gridItemImg.setImageDrawable(tintDrawable(itemImgs[index],mContext.getResources().getColorStateList(R.color.item_image_select)));

                gridItemTxt.setText(itemTitles[index]);
                gridItemTxt.setTextSize(sizeWidth);

                return view;
            }

            @Override
            public int getCount() {
                return itemTitles.length;
            }
        });

        mGridMenuLayout.setOnItemClickListener(new GridMenuLayout.OnItemClickListener() {

            public void onItemClick(View v, int index) {
                switch (index) {
                    case ITEM_CODE_ICC:
                        Intent iccIntent = new Intent(MainActivity.this, IccActivity.class);
                        startActivity(iccIntent);
                        break;
                    case ITEM_CODE_NFC:
                        Intent nfcIntent = new Intent(MainActivity.this, PiccActivity.class);
                        startActivity(nfcIntent);
                        break;
                    case ITEM_CODE_MCR:
                        Intent mcrIntent = new Intent(MainActivity.this, McrActivity.class);
                        startActivity(mcrIntent);
                        break;
                    case ITEM_CODE_PCI:
                        Intent pciIntent = new Intent(MainActivity.this, PciActivity.class);
                        startActivity(pciIntent);
                        break;
                    case ITEM_CODE_PRINT:
                        Intent printIntent = new Intent(MainActivity.this, PrintActivity.class);
                        startActivity(printIntent);
                        break;
                    case ITEM_CODE_SYS:
                        Intent sysIntent = new Intent(MainActivity.this, SysActivity.class);
                        startActivity(sysIntent);
                        break;
                    case ITEM_CODE_SCAN:
                        Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(scanIntent);
                        break;
                    case ITEM_CODE_UPDATE_OS:

                        new AlertDialog.Builder(mContext)
                                .setTitle(getResources().getString(R.string.upgrade_os))
                                .setMessage(R.string.upgradeTips)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent osIntent = new Intent(MainActivity.this, UpgradeOsActivity.class);
                                        startActivity(osIntent);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();


                        break;
                    case ITEM_CODE_EMV:
                        Intent emvIntent = new Intent(MainActivity.this, EmvTestActivity.class);
                        startActivity(emvIntent);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
