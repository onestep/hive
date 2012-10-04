package hive.game.providers;

import hive.game.providers.impl.NegaMaxAB;
import hive.game.Game;
import hive.game.Move;
import hive.intf.MoveProvider;
import hive.intf.Thinker;

public class NegaMaxMoveProvider
  implements MoveProvider
{
  private Thinker thinker;
  private Game g;
  private NegaMaxAB negamax;

  public NegaMaxMoveProvider(Game paramGame, Thinker paramThinker)
  {
    this.g = paramGame;
    this.thinker = paramThinker;
    this.negamax = new NegaMaxAB(paramGame, paramThinker);
  }

  public Move findMove(Game paramGame, int paramInt)
  {
    if (this.g != paramGame) {
      this.g = paramGame;
      this.negamax = new NegaMaxAB(paramGame, this.thinker);
    }
    return this.negamax.findMove(paramInt);
  }

  public void cleanUp()
  {
    this.negamax.interrupt();
  }

  public String getName()
  {
    return "Computer";
  }

  public void _break()
  {
    this.negamax.interrupt();
  }
}