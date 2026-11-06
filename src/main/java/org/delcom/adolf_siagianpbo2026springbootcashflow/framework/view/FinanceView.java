package org.delcom.adolf_siagianpbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.adolf_siagianpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.usecase.FinanceUseCase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * Menampilkan ringkasan keuangan, daftar transaksi, pencarian, pengurutan,
 * penambahan pemasukan/pengeluaran, dan penghapusan dengan konfirmasi.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private enum EntryKind {
        INCOME, EXPENSE
    }

    private static final String EMPTY_DATA = "- Data transaksi belum tersedia!";
    private static final String EMPTY_SEARCH = "- Transaksi tidak ditemukan!";

    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Select<SortOption> sortSelect = new Select<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span(EMPTY_DATA);
    private final Span totalCount = new Span("0");
    private final Span totalIncome = new Span();
    private final Span totalExpense = new Span();
    private final Span balanceValue = new Span();

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();

        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ---------------------------------------------------------------- ringkasan

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                createSummaryCard("Total Pemasukan", totalIncome, "#DCFCE7", "#16A34A", "#166534"),
                createSummaryCard("Total Pengeluaran", totalExpense, "#FEE2E2", "#DC2626", "#991B1B"),
                createSummaryCard("Saldo", balanceValue, "#FEF3C7", "#D97706", "#92400E")
        );
        summary.setWidthFull();
        summary.setSpacing(true);
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", accent);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("line-height", "1.1")
                .set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "0")
                .set("background", background)
                .set("border", "1px solid " + accent)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(financeUseCase.getAllTransactions().size()));
        totalIncome.setText(presenter.formatMoney(financeUseCase.getTotalIncome()));
        totalExpense.setText(presenter.formatMoney(financeUseCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(financeUseCase.getBalance()));
    }

    // --------------------------------------------------------------------- grid

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID")
                .setKey("id")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan")
                .setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(t -> presenter.formatMoney(t.getAmount()))
                .setHeader("Jumlah")
                .setKey("amount")
                .setAutoWidth(true)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getAmount));

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe")
                .setKey("type")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu")
                .setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan")
                .setKey("actions")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span createTypeBadge(Transaction transaction) {
        Span badge = new Span(presenter.formatType(transaction.getType()));
        boolean income = transaction.isIncome();
        badge.getStyle()
                .set("padding", "0.15rem 0.6rem")
                .set("border-radius", "999px")
                .set("font-size", "0.8rem")
                .set("font-weight", "600")
                .set("color", income ? "#166534" : "#991B1B")
                .set("background", income ? "#DCFCE7" : "#FEE2E2");
        return badge;
    }

    private HorizontalLayout createActionButtons(Transaction transaction) {
        Button deleteButton = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(transaction));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(deleteButton);
    }

    // ------------------------------------------------------------------ toolbar

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setEmptySelectionAllowed(true);
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(presenter::formatSortOption);
        sortSelect.addValueChangeListener(e -> applyFilter());
    }

    private HorizontalLayout buildToolbar() {
        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(), e -> applyFilter());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll());

        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                e -> openAddDialog(EntryKind.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                e -> openAddDialog(EntryKind.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, sortSelect, refreshButton, incomeButton, expenseButton);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    // ------------------------------------------------------------------ dialogs

    private void openAddDialog(EntryKind kind) {
        boolean income = kind == EntryKind.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();
        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setPlaceholder("contoh: 1000000");
        amountField.setWidthFull();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("450px");
        dialog.setMaxWidth("95vw");
        dialog.add(new VerticalLayout(descriptionField, amountField));

        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                BigDecimal amount = parseAmount(amountField.getValue());
                Transaction created = income
                        ? financeUseCase.addIncome(descriptionField.getValue(), amount)
                        : financeUseCase.addExpense(descriptionField.getValue(), amount);
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (NumberFormatException ex) {
                showError(presenter.showInvalidAmount());
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus transaksi \"" + transaction.getDescription()
                + "\" (ID " + transaction.getId() + ", " + presenter.formatMoney(transaction.getAmount()) + ")?"));

        Button confirmButton = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    // ------------------------------------------------------------------ actions

    /** Mengubah teks input menjadi BigDecimal; melempar NumberFormatException jika bukan angka. */
    private BigDecimal parseAmount(String text) {
        return new BigDecimal(text == null ? "" : text.trim());
    }

    private void deleteTransaction(Transaction transaction) {
        try {
            financeUseCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        } catch (ValidationException ex) {
            showError(presenter.showValidationError(ex.getMessage()));
        }
    }

    /** Menerapkan kata kunci pencarian dan opsi pengurutan yang sedang dipilih secara aman. */
    private void applyFilter() {
        String keyword = searchField.getValue();
        SortOption selectedSort = sortSelect.getValue();
        
        // Memanggil use case dengan pengaman pengecekan null pada sortOption
        List<Transaction> results = financeUseCase.findTransactions(keyword, selectedSort);
        
        boolean searching = keyword != null && !keyword.isBlank();
        emptyState.setText(searching ? EMPTY_SEARCH : EMPTY_DATA);
        refreshGrid(results, searching ? "Hasil Pencarian: \"" + keyword + "\"" : "Daftar Transaksi");
    }

    private void refreshAll() {
        searchField.clear();
        sortSelect.clear();
        emptyState.setText(EMPTY_DATA);
        refreshGrid(financeUseCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> transactions, String title) {
        grid.setItems(transactions);
        updateSummary();
        statusLabel.setText(transactions.isEmpty()
                ? title + " — " + emptyState.getText().replaceFirst("^- ", "")
                : title);
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}