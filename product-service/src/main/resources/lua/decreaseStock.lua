local stock_key = KEYS[1]
local quantity = tonumber(ARGV[1])

local current_stock = redis.call('GET', stock_key)

if not current_stock or tonumber(current_stock) < quantity then
    return -1 -- 재고 부족
else
    return redis.call('DECRBY', stock_key, quantity) -- 차감 후 남은 재고 반환
end
