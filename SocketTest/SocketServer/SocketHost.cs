using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Web.Script.Serialization;

namespace SocketServer
{
    public class SocketHost
    {
        private IDictionary<Socket, byte[]> socketClientSesson = new Dictionary<Socket, byte[]>();

        public int Port { get; set; }

        public void Start()
        {
            var socketThread = new Thread(() =>
            {
                Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                IPEndPoint iep = new IPEndPoint(IPAddress.Any, this.Port);

                //绑定到通道上
                socket.Bind(iep);

                //侦听
                socket.Listen(6);

                //通过异步来处理
                socket.BeginAccept(new AsyncCallback(Accept), socket);

            });

            socketThread.Start();

            Console.WriteLine("服务器已启动");
        }

        private void Accept(IAsyncResult ia)
        {
            Socket socket = ia.AsyncState as Socket;
            var client = socket.EndAccept(ia);

            socket.BeginAccept(new AsyncCallback(Accept), socket);

            byte[] buf = new byte[1024];
            this.socketClientSesson.Add(client, buf);

            try
            {
                client.BeginReceive(buf, 0, buf.Length, SocketFlags.None, new AsyncCallback(Receive), client);
                string sessionId = client.Handle.ToString();
                Console.WriteLine("客户端({0})已连接", sessionId);
            }
            catch (Exception ex)
            {
                Console.WriteLine("监听请求时出错：\r\n" + ex.ToString());
            }
        }

        private void Receive(IAsyncResult ia)
        {
            var client = ia.AsyncState as Socket;

            if (client == null || !this.socketClientSesson.ContainsKey(client))
            {
                return;
            }

            int count = client.EndReceive(ia);

            byte[] buf = this.socketClientSesson[client];

            if (count > 0)
            {
                try
                {
                    client.BeginReceive(buf, 0, buf.Length, SocketFlags.None, new AsyncCallback(Receive), client);
                    string context = Encoding.UTF8.GetString(buf, 0, count);
                    Console.WriteLine("接收的数据为:", context);

                    this.Response(client, context);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("接收的数据出错:\r\n{0}", ex.ToString());
                }
            }
            else
            {
                try
                {
                    string sessionId = client.Handle.ToString();
                    client.Disconnect(true);
                    this.socketClientSesson.Remove(client);
                    Console.WriteLine("客户端({0})已断开", sessionId);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("客户端已断开出错" + ex.ToString());
                }
            }
        }

        private void Response(Socket sender, string context)
        {
            SocketRequest request = null;
            JavaScriptSerializer jss = new JavaScriptSerializer();
            request = jss.Deserialize(context, typeof(SocketRequest)) as SocketRequest;

            if (request == null)
            {
                return;
            }

            var typeName = "SocketServer." + request.Method + "ResponseManager, SocketServer";
            Console.WriteLine("反射类名为：" + typeName);

            Type type = Type.GetType(typeName);
            if (type == null)
            {
                return;
            }

            var manager = Activator.CreateInstance(type) as IResponseManager;
            manager.Write(sender, this.socketClientSesson.Select(s => s.Key).ToList(),
                request.Param as IDictionary<string, object>);
        }
    }
}
