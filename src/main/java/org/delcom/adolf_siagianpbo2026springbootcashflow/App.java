package org.delcom.adolf_siagianpbo2026springbootcashflow;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@JsModule("./connection-indicator-accessibility.js")
@Meta(name = "description", content = "Catat pemasukan dan pengeluaran, cari transaksi, serta pantau saldo keuangan pribadi.")
public class App implements AppShellConfigurator {

    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }
}
