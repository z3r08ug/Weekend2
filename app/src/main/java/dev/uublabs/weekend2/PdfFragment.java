package dev.uublabs.weekend2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class PdfFragment extends Fragment implements View.OnClickListener
{

    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";
    private static final String FILENAME = "AndTutorials-3.9.pdf";
    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private ImageView mImageView;
    private Button mButtonPrevious;
    private Button mButtonNext;
    private int mPageIndex;

    public PdfFragment()
    {
        //blank required constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // bind Views
        mImageView = view.findViewById(R.id.ivPdf);
        mButtonPrevious = view.findViewById(R.id.btnPrevious);
        mButtonNext = view.findViewById(R.id.btnNext);
        // Bind button onclick listeners.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);

        //set page number to zero
        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState)
        {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        try
        {
            //open Renderer with pdf file and show first page
            openRenderer(getActivity());
            showPage(mPageIndex);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop()
    {
        //close the renderer
        try
        {
            closeRenderer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    private void openRenderer(Context context) throws IOException
    {
        //fetch pdf from assets folder
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists())
        {
            // Since PdfRenderer cannot handle the compressed asset file directly, copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(FILENAME);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1)
            {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // Create the pdfRenderer
        if (mFileDescriptor != null)
        {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    private void closeRenderer() throws IOException
    {
        if (null != mCurrentPage)
        {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    private void showPage(int index)
    {
        if (mPdfRenderer.getPageCount() <= index)
        {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage)
        {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);

        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);

        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        mImageView.setImageBitmap(bitmap);
        updateUi();
    }

    @SuppressLint("StringFormatInvalid")
    private void updateUi()
    {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        getActivity().setTitle(getString(R.string.app_name, index + 1, pageCount));
    }

    public int getPageCount()
    {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnPrevious:
            {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.btnNext:
            {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

}