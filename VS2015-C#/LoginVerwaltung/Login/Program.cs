using System;
using System.Windows.Forms;

namespace Login
{
    static class Program
    {
        /// <summary>
        /// Der Haupteinstiegspunkt für die Anwendung.
        /// </summary>
        [STAThread]
        static void Main()
        {
            // Thread.CurrentThread.CurrentUICulture = CultureInfo.GetCultureInfo("ja-JP");
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new frmLogin());
        }
    }
}
