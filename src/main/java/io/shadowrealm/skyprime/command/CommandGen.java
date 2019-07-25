package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.gen.IslandGenerator;
import mortar.api.sched.SR;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.compute.math.Profiler;
import mortar.lang.collection.Callback;
import mortar.lang.collection.FinalInteger;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandGen extends MortarCommand
{
	public CommandGen()
	{
		super("generate", "gen", "g");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!sender.hasPermission("sky.gentest"))
		{
			return true;
		}

		if(args.length > 0)
		{
			IslandGenerator g = new IslandGenerator(sender.player().getLocation().clone().subtract(0, 20, 0));

			for(String i : args)
			{
				if(i.startsWith("s:"))
				{
					g.setRadiusBlocks(Integer.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("d:"))
				{
					g.setDivisor(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("o:"))
				{
					g.setOctaves(Integer.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("n:"))
				{
					g.setNoise(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("c:"))
				{
					g.setDimension(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("q:"))
				{
					g.setFrequency(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("a:"))
				{
					g.setAmplifier(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("st:"))
				{
					g.setSquashTop(Double.valueOf(i.split(":")[1]));
				}

				if(i.startsWith("sb:"))
				{
					g.setSquashBottom(Double.valueOf(i.split(":")[1]));
				}
			}

			Profiler pr = new Profiler();
			pr.begin();
			FinalInteger vi = new FinalInteger(0);
			new SR()
			{
				@Override
				public void run()
				{
					if(vi.get() == 0)
					{
						sender.player().sendTitle("", C.AQUA + "" + C.BOLD + g.getStatus() + ": " + C.RESET + C.GRAY + F.pc(g.getProgress(), 0), 0, 5, 20);
					}

					else
					{
						sender.player().sendTitle("", C.AQUA + "" + C.BOLD + "Done", 0, 5, 20);

						cancel();
					}
				}
			};

			g.generate(new Callback<Integer>()
			{
				@Override
				public void run(Integer t)
				{
					vi.set(1);
					pr.end();
					sender.sendMessage("Generated Island in " + C.WHITE + F.time(pr.getMilliseconds(), 1));
					g.getCt().flush();
				}
			});
		}

		else
		{
			sender.sendMessage("At least specify s:<size>");
			sender.sendMessage("/skyprime generate " + C.WHITE + "s:<size> d:<divisor> o:<octaves> n:<noise> c:<clip> q:<freq> a:<amp> st:<squashtop> sb:<squashbottom>");
			sender.sendMessage("Example: s:16 d:1 o:7 n:5 c:0.5 q:0.5 a:0.5 st:2.5 sb:20");
		}

		return true;
	}
}
