using System;
using System.Data;
using System.Data.SQLite;


namespace Tools
{
    public class SQLException : Exception
    {
        public SQLException() : base() { }
        public SQLException(string msg) : base(msg) { }

    }
    public class SQLConnectionFailed : SQLException
    {
        public SQLConnectionFailed() : base() { }
        public SQLConnectionFailed(string msg) : base(msg) { }
    }
    public class SQLDatabaseClosed : SQLException
    {
        public SQLDatabaseClosed() : base() { }
        public SQLDatabaseClosed(string msg) : base(msg) { }
    }
    public class SQLQueryFailed : SQLException
    {
        public SQLQueryFailed() : base() { }
        public SQLQueryFailed(string msg) : base(msg) { }
    }

    abstract public class SQLAccess : IDisposable
    {
        protected dynamic conn = null;
        abstract public void Open(string path);
        abstract public DataTable Query(string query);
        abstract public int Execute(string query);
        public bool IsOpen() { return conn != null; }
        public void Dispose() { Close(); }
        public void Close() { if (IsOpen()) conn.Close(); conn = null; }
        public static string EscapeStr(string str) { return str.Replace("'", "\\'"); }

    }

    public class SQLiteAccess : SQLAccess
    {
        override public void Open(string path)
        {
            if (IsOpen())
                Close();
            try {
                conn = new SQLiteConnection("Data Source = " + path);
                conn.Open();
            } catch (Exception e) {
                throw new SQLConnectionFailed(e.Message);
            }
        }

        override public DataTable Query(String query)
        {
            if (!IsOpen())
                throw new SQLDatabaseClosed("Database closed");

            try {
                DataTable ret = new DataTable();
                using (SQLiteCommand cmd = new SQLiteCommand(query, this.conn)) {
                    ret.Load(cmd.ExecuteReader());
                }
                return ret;
            } catch (Exception e) {
                throw new SQLQueryFailed(e.Message);
            }
        }

        override public int Execute(String query)
        {
            if (!IsOpen())
                throw new SQLDatabaseClosed("Database closed");
            try {
                using (SQLiteCommand cmd = new SQLiteCommand(query, this.conn)) {
                    return cmd.ExecuteNonQuery();
                }
            } catch (Exception e) {
                throw new SQLQueryFailed(e.Message);
            }
        }
    }
}
