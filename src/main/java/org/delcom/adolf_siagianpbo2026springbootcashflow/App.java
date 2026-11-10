package org.delcom.adolf_siagianpbo2026springbootcashflow;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.AppShellSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
@JsModule("./connection-indicator-accessibility.js")
@Meta(name = "description", content = "Catat pemasukan dan pengeluaran, cari transaksi, serta pantau saldo keuangan pribadi.")
public class App implements AppShellConfigurator {

    private static final String PAGE_TITLE = "Cashflow | Catatan Keuangan";
    private static final String CANONICAL_URL = "https://ifs25025-pbo2026-springboot-cashflow.onrender.com/";

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setPageTitle(PAGE_TITLE);
        settings.addLink("canonical", CANONICAL_URL);
        settings.addLink("favicon.svg", Map.of(
                "rel", "icon",
                "type", "image/svg+xml"
        ));
    }

    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }
}
