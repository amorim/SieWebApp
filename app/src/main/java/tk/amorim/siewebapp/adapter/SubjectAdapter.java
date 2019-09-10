package tk.amorim.siewebapp.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.amorim.siewebapp.R;
import tk.amorim.siewebapp.interfaces.IDataChangedListener;
import tk.amorim.siewebapp.models.Subject;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<Subject> mSubjects;
    private IDataChangedListener listener;

    public SubjectAdapter(List<Subject> items, IDataChangedListener listener) {
        mSubjects = items;
        this.listener = listener;
    }

    public void updateList(List<Subject> list) {
        this.mSubjects = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {
        holder.mItem = mSubjects.get(position);
        holder.mNameView.setText(mSubjects.get(position).getName());
        holder.mCodeView.setText(mSubjects.get(position).getCode());
        holder.mWorkLoadView.setText(mSubjects.get(position).getCh() + "h");
        holder.mEdtMFView.setText(mSubjects.get(position).getAvaliacoes().getMf() + "");
        holder.mEdtMFView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        Double.parseDouble(charSequence.toString());
                    }
                    catch (Exception ex) {
                        System.out.println("K");
                        return;
                    }
                    double val = Double.parseDouble(charSequence.toString());
                    if (position >= mSubjects.size() || val < -1.0d || val > 10.0d)
                        return;
                    mSubjects.get(position).getAvaliacoes().setMf(Double.parseDouble(charSequence.toString()));
                    listener.updatedData();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mCodeView;
        public final TextView mWorkLoadView;
        public final EditText mEdtMFView;
        public Subject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.txtSubjectName);
            mCodeView = view.findViewById(R.id.txtSubjectCode);
            mWorkLoadView = view.findViewById(R.id.txtSubjectWorkload);
            mEdtMFView = view.findViewById(R.id.edtMF);
        }
    }
}
