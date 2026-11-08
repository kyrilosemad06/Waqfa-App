import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;

/** Local, offline owner panel. Never bundle the private key with the APK. */
public final class WaqfaOwnerPanel {
  private final JTextField keyPath = new JTextField("WAQFA_OWNER_PRIVATE_KEY.pem");
  private final JTextArea request = new JTextArea(4, 32);
  private final JSpinner days = new JSpinner(new SpinnerNumberModel(365, 1, 3650, 1));
  private final JSpinner transfers = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
  private final JTextArea result = new JTextArea(6, 32);
  private final JLabel state = new JLabel("ألصق كود طلب العميل ثم أصدر كود التفعيل.");

  private WaqfaOwnerPanel() {
    JFrame frame = new JFrame("WAQFA Owner Panel — Offline");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new JPanel(); panel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("المفتاح الخاص للمالك")); panel.add(keyPath);
    panel.add(Box.createVerticalStrut(10)); panel.add(new JLabel("كود طلب الجهاز من العميل"));
    request.setLineWrap(true); request.setWrapStyleWord(true); panel.add(new JScrollPane(request));
    JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT)); options.add(new JLabel("مدة الرخصة بالأيام")); options.add(days); options.add(new JLabel("عدد النقل الحالي")); options.add(transfers); panel.add(options);
    JButton issue = new JButton("إصدار كود التفعيل"); issue.addActionListener(e -> issue()); panel.add(issue);
    panel.add(Box.createVerticalStrut(8)); panel.add(state); panel.add(new JLabel("كود التفعيل الناتج"));
    result.setLineWrap(true); result.setWrapStyleWord(true); result.setEditable(false); panel.add(new JScrollPane(result));
    JButton copy = new JButton("نسخ كود التفعيل"); copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(result.getText()), null)); panel.add(copy);
    frame.setContentPane(panel); frame.pack(); frame.setSize(610, 560); frame.setLocationByPlatform(true); frame.setVisible(true);
  }
  private void issue() {
    try {
      String[] parts = request.getText().trim().split("\\|", -1);
      if (parts.length != 4 || !"WAQFA-R1".equals(parts[0]) || parts[1].isBlank() || parts[2].isBlank()) throw new IllegalArgumentException("كود طلب الجهاز غير صحيح.");
      long expiry = System.currentTimeMillis() + ((Number) days.getValue()).longValue() * 86_400_000L;
      String token = WaqfaLicenseManager.issueToken(Path.of(keyPath.getText().trim()), parts[1], parts[2], expiry, ((Number)transfers.getValue()).intValue(), 3, "premium");
      result.setText(token); state.setText("تم الإصدار محليًا — انسخ الكود وأرسله للعميل.");
    } catch (Exception ex) { state.setText("خطأ: " + ex.getMessage()); result.setText(""); }
  }
  public static void main(String[] args) { SwingUtilities.invokeLater(WaqfaOwnerPanel::new); }
}
