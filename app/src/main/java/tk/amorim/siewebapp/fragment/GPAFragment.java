package tk.amorim.siewebapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import tk.amorim.siewebapp.R;
import tk.amorim.siewebapp.interfaces.BoletimGetActivity;
import tk.amorim.siewebapp.models.Periodo;
import tk.amorim.siewebapp.util.Coef;


public class GPAFragment extends BoletimGenericFragment {

    CircleProgressView cpv;
    List<Periodo> boletim;

    private BoletimGetActivity actGetBoletim;

    public GPAFragment() {

    }

    public static GPAFragment newInstance() {
        GPAFragment fragment = new GPAFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gpa, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cpv = getView().findViewById(R.id.circleView);

        cpv.setRoundToWholeNumber(false);

        cpv.setDecimalFormat(new DecimalFormat("0.00"));
        cpv.setBarColor(ContextCompat.getColor(requireActivity(), R.color.gradient1), ContextCompat.getColor(requireActivity(), R.color.gradient2), ContextCompat.getColor(requireActivity(), R.color.gradient3));
        cpv.setRimColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
        cpv.setSpinBarColor(ContextCompat.getColor(requireActivity(), R.color.primary_darker));
        cpv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
        cpv.setSeekModeEnabled(false);
        cpv.setTextMode(TextMode.VALUE);
        cpv.setMaxValue(10);
        cpv.setUnitVisible(false);
        boletim = actGetBoletim.getBoletim();
        boletimReady(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BoletimGetActivity) {
            actGetBoletim = (BoletimGetActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BoletimGetActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actGetBoletim = null;
    }

    @Override
    public void boletimReceived(List<Periodo> boletim) {
        this.boletim = boletim;
        boletimReady(true);
    }

    private void boletimReady(boolean animated) {
        if (boletim == null)
            return;
        float coef = Coef.calculate(boletim);
        if (animated)
            cpv.setValueAnimated(coef);
        else
            cpv.setValue(coef);
    }




}
