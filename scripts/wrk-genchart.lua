-- wrk 压测 POST /chart/genchart（multipart + JWT，token 自行从登录接口复制）
-- 注意：URL 必须在 -- 之前，JWT 在 -- 之后（否则 wrk 会把 token 当成 URL报错）
--   wrk -t4 -c40 -d10s -s scripts/wrk-genchart.lua http://127.0.0.1:8080 -- '<JWT>'

local boundary = "----WrkGenchartBoundary"

function init(args)
  local token = args[1]
  if token == nil or token == "" then
    print("用法: wrk ... -s wrk-genchart.lua http://HOST:PORT -- '<JWT>' ")
    os.exit(1)
  end

  wrk.method = "POST"
  wrk.path = "/chart/genchart"
  wrk.headers["Authorization"] = "Bearer " .. token
  wrk.headers["Content-Type"] = "multipart/form-data; boundary=" .. boundary

  wrk.body =
    "--" .. boundary .. "\r\n" ..
    'Content-Disposition: form-data; name="req"' .. "\r\n\r\n" ..
    '{"name":"wrk","goal":"benchmark","chartType":"line"}' .. "\r\n" ..
    "--" .. boundary .. "\r\n" ..
    'Content-Disposition: form-data; name="file"; filename="t.csv"' .. "\r\n" ..
    "Content-Type: text/csv" .. "\r\n\r\n" ..
    "m,v\r\n1,10\r\n2,20\r\n" ..
    "--" .. boundary .. "--\r\n"
end

function request()
  return wrk.format(wrk.method, wrk.path, wrk.headers, wrk.body)
end
