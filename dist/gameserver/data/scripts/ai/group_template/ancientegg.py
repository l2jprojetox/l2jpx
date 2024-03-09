import sys
from net.l2jpx.gameserver.model.quest import State
from net.l2jpx.gameserver.model.quest import QuestState
from net.l2jpx.gameserver.model.quest.jython import QuestJython as JQuest
from net.l2jpx.gameserver.datatables import SkillTable
from java.lang import System

EGG = 18344

class AncientEgg(JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAttack (self,npc,player,damage,isPet):
   player.setTarget(player)
   player.doCast(SkillTable.getInstance().getInfo(5088,1))
   return

QUEST = AncientEgg(-1, "ancientegg", "ai")
CREATED = State('Start', QUEST)
QUEST.setInitialState(CREATED)

QUEST.addAttackId(EGG)
