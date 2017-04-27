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
    public partial class frmUserControl : Form
    {
        private DataControl control;
        private DataTable dt;

        private void setupForm()
        {
            dt = new DataTable();
            dt.Columns.Add(DataControl.Person.cUID, typeof(int));
            dt.Columns.Add(DataControl.Person.cNickname);
            dt.Columns.Add(DataControl.Person.cKennwort);
            dt.Columns.Add(DataControl.Person.cName);
            dt.Columns.Add(DataControl.Person.cVorname);
            dt.Columns.Add(DataControl.Person.cAdmin, typeof(bool));
            dt.Columns.Add(DataControl.Person.cAktiviert, typeof(bool));

            long MaxUID = 0;

            foreach (DataControl.Person foo in control.GetUsers()) {
                DataRow row = dt.NewRow();
                row[0] = foo.UID;
                row[1] = foo.Nickname;
                row[2] = foo.Kennwort;
                row[3] = foo.Name;
                row[4] = foo.Vorname;
                row[5] = foo.Admin;
                row[6] = foo.Aktiviert;
                dt.Rows.Add(row);

                if (foo.UID > MaxUID)
                    MaxUID = foo.UID;
            }

            foreach (DataColumn col in dt.Columns) {
                switch (col.ColumnName) {
                    case DataControl.Person.cAktiviert:
                    case DataControl.Person.cAdmin:
                        col.ReadOnly = false;
                        break;
                    default:
                        col.ReadOnly = true;
                        break;
                }
            }
            /* (unneeded)
                        DataColumn tmp = dt.Columns[DataControl.Person.cUID];
                        tmp.ReadOnly = true;
                        tmp.Unique = true;
                        tmp.AutoIncrement = true;
                        tmp.AutoIncrementSeed = MaxUID + 1;
            */
            dataUserControl.DataSource = dt;
        }

        public frmUserControl(ref DataControl control)
        {
            InitializeComponent();
            this.control = control;
            setupForm();
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            foreach (var foo in control.GetUsers()) {
                bool found = false;
                foreach (DataRow row in dt.Rows) {
                    if (foo.UID == (int)row[0]) {
                        foo.Admin = (bool)row[5];
                        foo.Aktiviert = (bool)row[6];
                        control.Update(foo);
                        found = true;
                        break;
                    }
                }
                if (found == false)
                    control.Delete(foo);
            }
            this.Close();
        }

        private void frmUserControl_Load(object sender, EventArgs e)
        {
            btnClose.Text = strings.close;
            btnCancel.Text = strings.cancel;
            this.Text = strings.adminpanel;
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
