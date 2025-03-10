from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import json

# 模拟的人物信息数据库
people_db = {
    "xiaoming": {"name": "XiaoMing", "city": "Beijing"},
    "lilei": {"name": "LiLei", "city": "Shanghai"},
    "hanmeimei": {"name": "HanMeimei", "city": "Guangzhou"},
}

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # 解析URL和查询参数
        parsed_url = urlparse(self.path)
        query_params = parse_qs(parsed_url.query)

        # 检查请求路径是否为 "/user"
        if parsed_url.path == "/user":
            # 获取查询参数中的 name 值
            name = query_params.get("name", [None])[0]

            # 设置响应状态码
            self.send_response(200)

            # 设置响应头
            self.send_header('Content-type', 'application/json')
            self.end_headers()

            # 根据 name 查询人物信息
            if name and name in people_db:
                response_data = people_db[name]
            else:
                response_data = {
                    "status": "error",
                    "message": "User not found"
                }

            # 将响应数据转换为JSON并发送
            self.wfile.write(json.dumps(response_data).encode('utf-8'))
        else:
            # 如果路径不是 "/user"，返回404错误
            self.send_error(404, "Not Found")

def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=8990):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print(f'Starting httpd server on port {port}...')
    httpd.serve_forever()

if __name__ == '__main__':
    run()