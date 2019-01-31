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
        for(index in 0 until eventTypes.size)
            if(eventType.toLowerCase() == eventTypes[index].toLowerCase())
                return index

        return -1
    }

    @JvmStatic
    fun getIdFromTypePrefix(eventType : String) : Int
    {
        for(index in 0 until eventTypes.size)
            if(eventType.substring(0, eventType.length).toLowerCase() == eventTypes[index].substring(0, eventType.length).toLowerCase())
                return index

        return -1
    }
}