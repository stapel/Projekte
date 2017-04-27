using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Login
{
    public partial class frmLogin : Form
    {
        private DataControl control = new DataControl();
        private Dictionary<string, int> FailedLogins = new Dictionary<string, int>();
        public frmLogin()
        {
            InitializeComponent();
            //            Properties.
            btnLogin.Text = strings.login;
            btnRegister.Text = strings.register;
            lblPassword.Text = strings.password;
            lblUsername.Text = strings.username;
            this.Text = strings.login;
        }

        private void btnLogin_Click(object sender, EventArgs e)
        {
            if (txtUser.Text == "")
                return;

            lblStatus.Text = "";

            try {
                control.Login(txtUser.Text, txtPass.Text);
            } catch (LoginControlException ex) {
                lblStatus.Text = strings.denied + " (" + ex.Message + ")";

                if (!FailedLogins.ContainsKey(txtUser.Text))
                    FailedLogins.Add(txtUser.Text, 0);
                if (++FailedLogins[txtUser.Text] == 3)
                    control.Deactivate(txtUser.Text);
                return;
            } catch (Exception ex) {
                lblStatus.Text = strings.failedlogin + " (" + ex.Message + ")";
                return;
            }

            if (FailedLogins.ContainsKey(txtUser.Text))
                FailedLogins.Remove(txtUser.Text);

            frmUserInfo info = new frmUserInfo(ref control, this);
            info.Show();
            this.Hide();
        }

        private void btnRegister_Click(object sender, EventArgs e)
        {
            if (txtUser.Text == "" || txtPass.Text == "") {
                lblStatus.Text = strings.userpassempty;
                return;
            }
            control.AddUser(txtUser.Text, txtPass.Text);
        }
    }
}