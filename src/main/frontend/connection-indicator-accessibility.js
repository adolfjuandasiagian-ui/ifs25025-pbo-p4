(() => {
  document.documentElement.lang = "id";

  function labelConnectionIndicator() {
    const indicator = document.querySelector("vaadin-connection-indicator");
    if (!indicator) {
      return false;
    }

    indicator.setAttribute("role", "status");
    indicator.setAttribute("aria-label", "Status koneksi");
    return true;
  }

  if (!labelConnectionIndicator()) {
    const observer = new MutationObserver(() => {
      if (labelConnectionIndicator()) {
        observer.disconnect();
      }
    });
    observer.observe(document.documentElement, { childList: true, subtree: true });
  }
})();
