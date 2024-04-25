package com.example.quizappbycouchbase

object Constants{
    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val CORRECT_ANSWERS: String = "correct_answers"
    const val CATEGORY: String = "category"
    fun getQuestions(): ArrayList<Questions>{
        val questionList = ArrayList<Questions>()
        val que1 = Questions(1,"Charizard is an evolution of what?","Squirtle","Venusaur","Charmeleon","Metapod",3,"pokemon")
        val que2 = Questions(2,"What uses the transform move to become a copy of its opponent?","Ditto","Twinny","Doubler","Doppelganger",1,"pokemon")
        val que3 = Questions(3,"Which Pokémon did Ash capture as his first catch?","Pikachu","Caterpie","Bulbasaur","Squirtle",2,"pokemon")
        val que4 = Questions(4,"The first Pokemon games were for which console?","DS","Vita","Game Gear","Game Boy",4,"pokemon")
        val que5 = Questions(5,"Which Pokémon does Misty primarily use as her main companion?","Starmie","Psyduck","Togepi","Staryu",4,"pokemon")
        questionList.add(que1)
        questionList.add(que2)
        questionList.add(que3)
        questionList.add(que4)
        questionList.add(que5)
        return questionList
    }
}