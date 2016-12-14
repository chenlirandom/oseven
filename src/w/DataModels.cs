using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;

namespace o7
{
    public class HResult
    {
        public static HResult S_OK = new HResult(0);
        public static HResult E_FAIL = new HResult(-1, "Unknown failure");

        public int ErrorCode { get; private set; }
        public string ErrorMessage { get; private set; }
        public bool Succeeded { get { return this.ErrorCode >= 0; } }
        public bool Failed { get { return this.ErrorCode < 0; } }

        public HResult(int code)
        {
            this.ErrorCode = code;
            if (code < 0)
            {
                this.ErrorMessage = string.Format("Error(hr = 0x{0:X8}", code);
            }
            else
            {
                this.ErrorMessage = string.Empty;
            }
        }

        public HResult(int code, string message)
        {
            this.ErrorCode = code;
            this.ErrorMessage = message;
        }

        public HResult(string errorMessage)
        {
            this.ErrorCode = -1;
            this.ErrorMessage = errorMessage;
        }

        public HResult(Exception ex)
        {
            this.ErrorCode = ex.HResult;
            this.ErrorMessage = ex.Message;
        }
    }

    public class Character : DependencyObject
    {
        public const long ChenLi = 96612600;

        public long Id { get; private set; }

        public string Name { get; private set; }

        public string PublicInfoResponse { get; private set; }

        public Character(long id)
        {
            InitEmptyCharacter(id);

            var request = HttpWebRequest.Create("https://esi.tech.ccp.is/latest/characters/" + id + "/?datasource=tranquility");
            try
            {
                var response = request.GetResponse();
                var data = new byte[response.ContentLength];
                response.GetResponseStream().Read(data, 0, data.Length);
                this.PublicInfoResponse = Encoding.UTF8.GetString(data);
            }
            catch(Exception ex)
            {
                this.PublicInfoResponse = ex.ToString();
            }
        }

        void InitEmptyCharacter(long id)
        {
            this.Id = id;
            this.Name = string.Empty;
            this.PublicInfoResponse = string.Empty;
        }
    }

    // Stores application wide global variables.
    public class Globals
    {
        public List<Character> Characters { get; private set; }
    }
}
