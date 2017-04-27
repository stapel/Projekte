using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Tools;

namespace Login
{
    public class LoginControlException : Exception
    {
        public LoginControlException() : base() { }
        public LoginControlException(string msg) : base(msg) { }

    }
    public class LoginFailed : LoginControlException
    {
        public LoginFailed() : base() { }
        public LoginFailed(string msg) : base(msg) { }
    }

    public class UserNotActiv : LoginControlException
    {
        public UserNotActiv() : base() { }
        public UserNotActiv(string msg) : base(msg) { }
    }


    public class DataControl
    {
        private const string DBPATH = @"D:\UserAccounts.sqlite";
        private SQLAccess sql = new SQLiteAccess();
        private Person User = null;

        public class Person
        {
            public Person()
            {
                UID = 0;
                Name = "";
                Email = "";
                Vorname = "";
                Telefon = "";
                Nickname = "";
                Kennwort = "";
                Admin = false;
                Aktiviert = false;
            }
            public Person(DataRow row)
            {
                UID = (long)row[cUID];
                Name = (string)row[cName];
                Email = (string)row[cEmail];
                Vorname = (string)row[cVorname];
                Telefon = (string)row[cTelefon];
                Nickname = (string)row[cNickname];
                Kennwort = (string)row[cKennwort];
                Admin = (bool)row[cAdmin];
                Aktiviert = (bool)row[cAktiviert];
            }

            public const string cUID = "UID";
            public const string cName = "Name";
            public const string cEmail = "Email";
            public const string cVorname = "Vorname";
            public const string cKennwort = "Kennwort";
            public const string cNickname = "Nickname";
            public const string cTelefon = "Telefon";
            public const string cAdmin = "Admin";
            public const string cAktiviert = "Aktiviert";

            public long UID { set; get; }
            public string Name { set; get; }
            public string Email { set; get; }
            public string Vorname { set; get; }
            public string Telefon { set; get; }
            public string Nickname { set; get; }
            public string Kennwort { set; get; }
            public bool Admin { set; get; }
            public bool Aktiviert { set; get; }
        }

        public DataControl()
        {
            sql.Open(DBPATH);
        }

        public Person[] GetUsers()
        {
            DataTable tbl = sql.Query("select * from user;");
            Person[] people = new Person[tbl.Rows.Count];

            for (int i = 0; i < tbl.Rows.Count; i++)
                people[i] = new Person(tbl.Rows[i]);
            return people;
        }

        public Person GetUser(int UID)
        {
            DataTable tbl = sql.Query("select * from user where "
                + Person.cUID + "= '"
                + UID.ToString()
                + "';");
            return new Person(tbl.Rows[0]);
        }

        public Person GetUser() { return this.User; }
        public Person GetUser(string user, string password)
        {
            DataTable tbl = sql.Query("select * from user where "
                + Person.cNickname + "= '"
                + SQLAccess.EscapeStr(user)
                + "' and " + Person.cKennwort + " = '"
                + SQLAccess.EscapeStr(password) + "';");
            if (tbl.Rows.Count == 0)
                return null;

            return new Person(tbl.Rows[0]);
        }

        public void Login(string Username, string Password)
        {
            try {
                this.User = GetUser(Username, Password);
            } catch (Exception e) {
                this.User = null;
            }
            if (this.User == null)
                throw new LoginFailed("Nutzer konnte nicht angemeldet werden");

            if (!this.User.Aktiviert)
                throw new UserNotActiv("Nutzer ist nicht aktiviert");
        }

        private long MaxID()
        {
            long maxID = 0;
            foreach (Person person in GetUsers())
                if (person.UID > maxID)
                    maxID = person.UID;

            return maxID;
        }

        public void AddUser(string Username, string Password)
        {
            Person person = new Person();
            person.UID = MaxID() + 1;
            person.Nickname = Username;
            person.Kennwort = Password;
            AddUser(person);
        }

        public void AddUser(Person person)
        {
            try {
                sql.Execute("insert into user values ('"
                    + person.UID.ToString() + "', '"
                    + SQLAccess.EscapeStr(person.Name) + "', '"
                    + SQLAccess.EscapeStr(person.Vorname) + "', '"
                    + SQLAccess.EscapeStr(person.Telefon) + "', '"
                    + SQLAccess.EscapeStr(person.Email) + "', '"
                    + SQLAccess.EscapeStr(person.Nickname) + "', '"
                    + SQLAccess.EscapeStr(person.Kennwort) + "', '"
                    + (person.Admin ? "True" : "False") + "', '"
                    + (person.Aktiviert ? "True" : "False") + "');");

            } catch (Exception e) {
                throw e;
            }
        }
        public void Update()
        {
            if (this.User != null)
                Update(this.User);
        }

        public void Deactivate(string Username)
        {
            sql.Execute("update user set " 
                + Person.cAktiviert + " = 'False'"
                + " where " 
                + Person.cNickname + " = '" + SQLAccess.EscapeStr(Username) + "';");
        }

        public void Delete(long UID)
        {
            sql.Execute("delete from user where "
                + Person.cUID + " = " + UID.ToString() + ";");
        }
        public void Delete(Person person)
        {
            Delete(person.UID);
        }

        public void Update(Person person)
        {
            sql.Execute("update user set "
                + Person.cName + " = '" + SQLAccess.EscapeStr(person.Name) + "'"
                + ", "
                + Person.cVorname + " = '" + SQLAccess.EscapeStr(person.Vorname) + "'"
                + ", "
                + Person.cTelefon + " = '" + SQLAccess.EscapeStr(person.Telefon) + "'"
                + ", "
                + Person.cEmail + " = '" + SQLAccess.EscapeStr(person.Email) + "'"
                + ", "
                + Person.cNickname + " = '" + SQLAccess.EscapeStr(person.Nickname) + "'"
                + ", "
                + Person.cKennwort + " = '" + SQLAccess.EscapeStr(person.Kennwort) + "'"
                + ", "
                + Person.cAdmin + " = '" + (person.Admin ? "True" : "False") + "'"
                + ", "
                + Person.cAktiviert + " = '" + (person.Aktiviert ? "True" : "False") + "'"
                + "where " + Person.cUID + " = " + person.UID.ToString() + ";");
        }
    }
}
