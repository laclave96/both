package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class FaqDialog {

    public void showDialog(Activity activity) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.faq_dialog);

        TextView close = dialog.findViewById(R.id.cancel);

        RecyclerView recyclerView = dialog.findViewById(R.id.faqRecycler);

        ArrayList<FaqItem> faqItems = new ArrayList<>();

        faqItems.add(new FaqItem("¿Cómo puedo buscar personas?", "Para buscar personas siga los pasos que se le indican, primero complete su cuenta y luego seleccione los párametros de búsqueda.", 0));
        faqItems.add(new FaqItem("¿Cómo puedo chatear con alguien?", "Para chatear con otro usuario debe hacer un match con él. Para esto deben darse Like mutuamente (los dos/el uno al otro).", 0));
        faqItems.add(new FaqItem("¿Qué significa que alguien me dé un like?", "Un like es un clara demostración de que esa persona esta interesada en conocerte e interactuar contigo. Por tanto, si el deseo es mutuo, dale un buen like para que puedan chatear y conseguir lo que buscan.", 0));
        faqItems.add(new FaqItem("¿Por qué me dice que excedí el límite diario?", "En una búsqueda diaria puede cargar hasta 50 nuevas personas, pero si sus párametros lo limitan a pocas personas, consume igual el límite diario aunque no haya llegado a 50.", 0));
        faqItems.add(new FaqItem("¿Tengo que pagar para usar la app?", "No, la app es completamente funcional en su version estándar, la versión premium desbloquea los beneficios expuestos en el apartado premium, y los que vayamos integrando, que pudieran ser por su sugerencia.", 0));
        faqItems.add(new FaqItem("¿Por qué se me repiten las personas que ya vi?", "Hasta el momento en cada carga se excluyen las personas de la búsqueda anterior y se sortean nuevas personas y personas que pudiste ver en varias cargas anteriores.", 0));

        FaqAdapter faqAdapter = new FaqAdapter(faqItems, activity.getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(faqAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = MainActivity.screenHeight;
        bottomSheet.setLayoutParams(layoutParams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }
}
