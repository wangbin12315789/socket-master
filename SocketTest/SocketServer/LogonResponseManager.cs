using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Web.Script.Serialization;
using System.Collections;

namespace SocketServer
{
    public class LogonResponseManager : IResponseManager
    {
        public void Write(System.Net.Sockets.Socket sender, IList<System.Net.Sockets.Socket> cliens, IDictionary<string, object> param)
        {
            Console.WriteLine("客户端({0})登陆", sender.Handle);
            var response = new SocketResponse
            {
                Method = "Logon",
                DateTime = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                Result = new { UserName = param["UserName"].ToString() }
            };

            JavaScriptSerializer jss = new JavaScriptSerializer();
            string context = jss.Serialize(response);
            Console.WriteLine("登陆发送的数据为:{0}", context);
            sender.Send(Encoding.UTF8.GetBytes(context + "\n"));
        }
    }
}
