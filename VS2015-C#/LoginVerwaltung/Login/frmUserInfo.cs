using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Login
{
    public partial class frmUserInfo : Form
    {
        private DataControl control;
        private Form parent;

        private void setupForm()
        {
            DataControl.Person user = control.GetUser();

            txtUser.Text = user.Nickname;
            txtName.Text = user.Name;
            txtVorname.Text = user.Vorname;
            txtTelefon.Text = user.Telefon;
            txtEmail.Text = user.Email;
            txtPassword.Text = user.Kennwort;

            if (user.Admin == true)
                btnAdmin.Visible = true;
        }
        public frmUserInfo(ref DataControl control, Form parent)
        {
            this.control = control;
            this.parent = parent;
            InitializeComponent();
            setupForm();

            btnAdmin.Text = strings.adminpanel;
            btnChange.Text = strings.change;
            btnClose.Text = strings.close;

            label1.Text = strings.username;
            label2.Text = strings.surname;
            label3.Text = strings.forname;
            label4.Text = strings.email;
            label5.Text = strings.phone;
            label6.Text = strings.newpassword2;
            label7.Text = strings.newpassword;
            label8.Text = strings.password;
            this.Text = strings.yourdata;
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            Application.Exit();
            //this.Close();
        }

        private void btnChange_Click(object sender, EventArgs e)
        {
            DataControl.Person user = this.control.GetUser();

            if (txtNewPassword.Text != "" && txtNewPassword2.Text != "") {
                if (txtNewPassword.Text != txtNewPassword2.Text) {
                    MessageBox.Show(strings.newpassword + " != " + strings.newpassword2);
                    return;
                }
                user.Kennwort = txtNewPassword.Text;
            }
            user.Nickname = txtUser.Text;
            user.Name = txtName.Text;
            user.Vorname = txtVorname.Text;
            user.Telefon = txtTelefon.Text;
            user.Email = txtEmail.Text;

            control.Update(user);
        }

        private void btnAdmin_Click(object sender, EventArgs e)
        {
            if (this.control.GetUser().Admin == false)
                return;

            Form form = new frmUserControl(ref this.control);
            form.ShowDialog();
        }

        private void frmUserInfo_FormClosed(object sender, FormClosedEventArgs e)
        {
            if (parent != null)
                parent.Show();
        }

        private void frmUserInfo_Load(object sender, EventArgs e)
        {

        }
    }
}
