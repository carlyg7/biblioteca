    package com.example.biblioteca_cm.Controlador;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentPagerAdapter;

    import com.example.biblioteca_cm.MainCatalogo;
    import com.example.biblioteca_cm.MainDatosUsuarios;
    import com.example.biblioteca_cm.MainReservas;

    public class PagerController extends FragmentPagerAdapter {
        int numoftabs;

        public PagerController(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.numoftabs = behavior;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
           switch (position){
               case 0:
                    return new MainCatalogo();
               case 1:
                    return new MainReservas();
               case 2:
                    return new MainDatosUsuarios();
               default:
                   return null;

           }
        }

        @Override
        public int getCount() {
            return numoftabs;
        }
    }
