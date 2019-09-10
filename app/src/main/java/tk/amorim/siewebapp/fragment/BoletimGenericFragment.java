package tk.amorim.siewebapp.fragment;

import androidx.fragment.app.Fragment;

import java.util.List;

import tk.amorim.siewebapp.models.Periodo;

public abstract class BoletimGenericFragment extends Fragment {
    public abstract void boletimReceived(List<Periodo> boletim);
}
