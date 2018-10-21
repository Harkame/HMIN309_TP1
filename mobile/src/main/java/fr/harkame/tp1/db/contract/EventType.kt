package fr.harkame.tp1.db.contract

object EventType {

    val eventTypes = arrayOf("Sport", "Rendez-vous", "Raid")

    @JvmStatic
    fun getTypeFromID(id : Int) : String
    {
        return eventTypes[id]
    }

    @JvmStatic
    fun getIDFromType(eventType : String) : Int
    {
        for(index in 0..eventTypes.size)
            if(eventType.toLowerCase().equals(eventTypes[index].toLowerCase()))
                return index

        return -1
    }
}