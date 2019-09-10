package tk.amorim.siewebapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tk.amorim.siewebapp.R;
import tk.amorim.siewebapp.adapter.SubjectAdapter;
import tk.amorim.siewebapp.interfaces.BoletimGetActivity;
import tk.amorim.siewebapp.interfaces.IDataChangedListener;
import tk.amorim.siewebapp.models.Periodo;
import tk.amorim.siewebapp.util.Coef;

public class SimulatorFragment extends BoletimGenericFragment implements IDataChangedListener {
    List<Periodo> boletim;
    LinearLayout data;
    ProgressBar progressBar;
    Spinner spinner;
    RecyclerView list;
    DecimalFormat df;

    private BoletimGetActivity actGetBoletim;

    public SimulatorFragment() {
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
    }

    public static SimulatorFragment newInstance() {
        SimulatorFragment fragment = new SimulatorFragment();
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
        return inflater.inflate(R.layout.fragment_simulator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = getActivity().findViewById(R.id.layout_simulator);
        list = getActivity().findViewById(R.id.subjectList);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar = getActivity().findViewById(R.id.progress_bar_simulator);
        spinner = getActivity().findViewById(R.id.spinnerPeriodos);
        showLoading();
        boletim = actGetBoletim.getBoletim();
        boletimReady();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fillListWithData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void showLoading() {
        data.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showData() {
        progressBar.setVisibility(View.GONE);
        data.setVisibility(View.VISIBLE);
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
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void boletimReceived(List<Periodo> boletim) {
        this.boletim = boletim;
        boletimReady();
    }

    private void boletimReady() {
        if (boletim == null)
            return;
        ArrayList<String> periodos = new ArrayList<>();
        for (Periodo p : boletim)
            periodos.add(p.getAno() + " - " + p.getSemestre());
        if (getActivity() == null)
            return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periodos);
        spinner.setAdapter(adapter);
        showData();
        fillListWithData();
    }

    private void fillListWithData() {
        int index = spinner.getSelectedItemPosition();
        this.list.setAdapter(new SubjectAdapter(boletim.get(index).getSubjects(), this));
        updatedData();
    }

    @Override
    public void updatedData() {
        FragmentActivity act = getActivity();
        if (act != null) {
            act.setTitle("GPA: " + df.format(Coef.calculate(boletim)));
        }
    }
}
