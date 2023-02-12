open class Tea(val cost: Int, val volume: Int) {
    override fun toString(): String {
        return "cost=$cost, volume=$volume"
    }
}

class BlackTea(val val_cost: Int, val val_volume: Int) : Tea(val_cost, val_volume) {
    override fun toString(): String {
        return "BlackTea{cost=$val_cost, volume=$val_volume}"
    }
}

