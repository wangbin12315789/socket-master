using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Web.Script.Serialization;
using System.Collections;
using System.Threading.Tasks;

namespace SocketServer
{
    public class SendResponseManager : IResponseManager
    {
        public void Write(System.Net.Sockets.Socket sender, IList<System.Net.Sockets.Socket> cliens, IDictionary<string, object> param)
        {
            Console.WriteLine("客户端({0})发送消息", sender.Handle);
            var msgList = param["Message"] as IEnumerable<object>;
            if (msgList == null)
            {
                return;
            }

            var response = new SocketResponse
            {
                Method = "Send",
                DateTime = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                Result = new
                { 
                    UserName = param["UserName"].ToString(),
                    Message = msgList.Select(s => s.ToString()).ToArray() 
                }
            };

            JavaScriptSerializer jss = new JavaScriptSerializer();
            string context = jss.Serialize(response);
            Console.WriteLine("消息发送的数据为:{0}", context);

            Parallel.ForEach(cliens, (item) =>
            {
                try
                {
                    item.Send(Encoding.UTF8.GetBytes(context + "\n"));
                }
                catch { };
            });
        }
    }
}
