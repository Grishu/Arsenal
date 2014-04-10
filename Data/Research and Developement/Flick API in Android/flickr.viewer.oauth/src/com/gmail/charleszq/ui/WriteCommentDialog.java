/**
 * 
 */

package com.gmail.charleszq.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.task.WriteCommentTask;

/**
 * @author charles
 */
public class WriteCommentDialog extends DialogFragment {

    private Button mOKButton, mCancelButton;
    private EditText mCommentField;
    private String mPhotoId;

    public WriteCommentDialog(String photoId) {
        this.mPhotoId = photoId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setTitle(
                getActivity().getString(R.string.dlg_title_write_comment));

        View view = inflater.inflate(R.layout.write_comment_dlg, null);
        mCommentField = (EditText) view.findViewById(R.id.edit_comment);
        mOKButton = (Button) view.findViewById(R.id.btn_comment_ok);
        mCancelButton = (Button) view.findViewById(R.id.btn_cancel);
        mOKButton.setOnClickListener(mClickListener);
        mCancelButton.setOnClickListener(mClickListener);

        return view;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == mCancelButton) {
                WriteCommentDialog.this.dismiss();
            } else if (v == mOKButton) {
                onWriteComment();
            }
        }

    };

    /**
     * Writes the comment.
     */
    private void onWriteComment() {
        String comment = mCommentField.getText().toString();
        if (comment == null || comment.trim().length() == 0) {
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(R.string.empty_comment_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FlickrViewerApplication app = (FlickrViewerApplication) getActivity().getApplication();
        String token = app.getFlickrToken();
        String secret = app.getFlickrTokenSecret();

        WriteCommentTask task = new WriteCommentTask(this);
        task.execute(mPhotoId, comment, token, secret);
    }

}
